/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.library.test.models;

import org.json.JSONException;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.common.network.AVSServer;
import org.openmidaas.library.model.core.AbstractAttribute;
import org.openmidaas.library.model.core.CompleteAttributeVerificationDelegate;
import org.openmidaas.library.model.core.CompleteVerificationCallback;
import org.openmidaas.library.model.core.InitializeAttributeVerificationDelegate;
import org.openmidaas.library.model.core.InitializeVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSError;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;

import com.loopj.android.http.AsyncHttpResponseHandler;

public class MockAttribute extends AbstractAttribute<String> implements InitializeAttributeVerificationDelegate, CompleteAttributeVerificationDelegate{

	private final String TAG = "MockAttribute";
	
	public MockAttribute() {
		mName = "MockAttribute";
		mState = ATTRIBUTE_STATE.NOT_VERIFIED;
		mSignedToken = "1234.1234.1234";
		mValue = "MockAttribute";
		mInitVerificationDelegate = this;
		mCompleteVerificationDelegate = this;
	}
	
	@Override
	protected boolean validateAttribute(String value) {
		return true;
	}

	@Override
	public void completeVerification(final AbstractAttribute<?> attribute,
			final String code, final CompleteVerificationCallback callback) {
		try {
			MIDaaS.logDebug(TAG, "Completing attribute verification");
			AVSServer.completeAttributeVerification(attribute, code, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(String response) {
					MIDaaS.logDebug(TAG, "Attribute verified successfully");
					attribute.setSignedToken(response);
					attribute.setPendingData(null);
					
					try {
						if(AttributePersistenceCoordinator.saveAttribute(attribute)) {
							callback.onSuccess();
						} else {
							MIDaaS.logError(TAG, "Attribute could not be saved");
							callback.onError(new MIDaaSException(MIDaaSError.DATABASE_ERROR));
						}
						
					} catch (MIDaaSException e) {
						MIDaaS.logError(TAG, e.getError().getErrorMessage());
						callback.onError(e);
					}
				}
				
				@Override
				public void onFailure(Throwable e, String response){
					MIDaaS.logError(TAG, response);
					callback.onError(new MIDaaSException(MIDaaSError.SERVER_ERROR));
				}
			});
		} catch (JSONException e1) {
			MIDaaS.logError(TAG, e1.getMessage());
			callback.onError(null);
		}
	}

	@Override
	public void startVerification(final AbstractAttribute<?> attribute,
			final InitializeVerificationCallback callback) {
		try {
			AVSServer.startAttributeVerification(attribute, new AsyncHttpResponseHandler() {
				
				@Override
				public void onSuccess(String response) { 
					if((!(response.isEmpty())) ) {
						attribute.setPendingData(response);
						// it is important that we guarantee that the data is persisted before we return. 
						try {
							if(AttributePersistenceCoordinator.saveAttribute(attribute)) {
								MIDaaS.logDebug(TAG, "done init email verification.");
								callback.onSuccess();
							} else {
								MIDaaS.logError(TAG, "error saving email attribute.");
								callback.onError(new MIDaaSException(MIDaaSError.DATABASE_ERROR));
							}
						} catch (MIDaaSException e) {
							MIDaaS.logError(TAG, e.getError().getErrorMessage());
							callback.onError(e);
						}
						callback.onSuccess();
						
					} else {
						MIDaaS.logError(TAG, "Server returned an empty response.");
						callback.onError(new MIDaaSException(MIDaaSError.SERVER_ERROR));
					}
				}
				
				@Override
				public void onFailure(Throwable e, String response){
					MIDaaS.logError(TAG, response);
					callback.onError(new MIDaaSException(MIDaaSError.SERVER_ERROR));
				}
			});
			
		} catch (JSONException e) {
			MIDaaS.logError(TAG, e.getMessage());
			callback.onError(null);
		}
	}
	
	@Override
	public void startVerification(InitializeVerificationCallback callback) throws UnsupportedOperationException {
		this.startVerification(this, callback);
	}
	
	@Override
	public void setPendingData(String data) {
		mPendingData = data;
	}
}
