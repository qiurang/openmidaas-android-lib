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
package org.openmidaas.library.common.network;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.authentication.AuthenticationManager;
import org.openmidaas.library.authentication.core.AccessToken;
import org.openmidaas.library.common.Constants;
import org.openmidaas.library.common.network.RetriableTaskController.RetriableTaskCallback;
import org.openmidaas.library.model.core.MIDaaSError;
import org.openmidaas.library.model.core.MIDaaSException;

import com.loopj.android.http.AsyncHttpResponseHandler;

public  class AVSPostDataTask extends Task{
	
	private final String TAG = "AVSRetryTask";
	
	private String mUrl;
	
	private HashMap<String, String> mHeaders;
	
	private JSONObject mDataToPost;
	
	//private AsyncHttpResponseHandler mResponseHandler;
	
	public AVSPostDataTask(String url,  HashMap<String, String> headers, JSONObject data){//, AsyncHttpResponseHandler responseHandler) {
		this.mUrl = url;
		this.mHeaders = headers;
		this.mDataToPost = data;
		//this.mResponseHandler = responseHandler;
	}
	
	

	@Override
	protected void execute(final RetryHandler mRetryHandler) {
		final CountDownLatch latch = new CountDownLatch(1);
		AccessToken token = AuthenticationManager.getInstance().getAccessToken();
		if(token == null) {
			MIDaaS.logError(TAG, "Error getting access token. Access token is null");
			mRetryHandler.onError(new MIDaaSException(MIDaaSError.ERROR_AUTHENTICATING_DEVICE), "");
			
		} else {
			try {
				ConnectionManager.postRequest(true, mUrl, Utils.getAuthHeader(token), mDataToPost, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) { 
						mRetryHandler.onSuccess(response);
						mRetryHandler.terminate();
						latch.countDown();
					}
				
					@Override
					public void onFailure(Throwable e, String response){
						try {
							JSONObject object = new JSONObject(response);
							// check for access token error here. 
							if(object.has("error") && !(object.isNull("error"))) {
								
								AuthenticationManager.getInstance().setAccessToken(null);
								latch.countDown();
							} else {
								mRetryHandler.onError(e, response);
								mRetryHandler.terminate();
								latch.countDown();
								
							}
						} catch(JSONException exception) {
							MIDaaS.logError(TAG, exception.getMessage());
							mRetryHandler.onError(e, response);
							mRetryHandler.terminate();
							latch.countDown();
						}
					}
				});
				latch.await();
				return;
				
			} catch (InterruptedException e1) {
				
				latch.countDown();
				
			}
		}
	}
}
