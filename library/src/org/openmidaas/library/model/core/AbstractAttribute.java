/*******************************************************************************
 * Copyright 2013 SecureKey Technologies Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *   
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.openmidaas.library.model.core;

import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.common.Constants;
import org.openmidaas.library.common.Constants.ATTRIBUTE_STATE;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;

/**
 * Abstract class that defines an attribute
 * @param <T>
 */

public abstract class AbstractAttribute<T> {
	
	private final String TAG = "AbstractAttribute";
	
	protected long mId = -1;
	
	protected InitializeAttributeVerificationDelegate mInitVerificationDelegate = null;
	
	protected CompleteAttributeVerificationDelegate mCompleteVerificationDelegate = null;
	
	protected String mName;
	
	protected T mValue = null;
	
	private String mLabel = null;
	
	protected String mSignedToken = null;
	
	protected String mPendingData = null;
	
	protected String mVerificationMethod = null;
	
	protected ATTRIBUTE_STATE mState = ATTRIBUTE_STATE.NOT_VERIFIABLE;
	
	/**
	 * Returns the attribute name
	 * @return attribute name
	 */
	public String getName() {
		return mName;
	}

	/**
	 * Returns the attribute's label. This is what is 
	 * shown to the user
	 * @return attribute's label.
	 */
	public String getLabel() {
		return mLabel;
	}

	/**
	 * Sets the attribute's label.
	 * @param label
	 */
	public void setLabel(String label) {
		this.mLabel = label;
	}

	/**
	 * Returns the signed token for the attribute
	 * @return signed token
	 */
	public String getSignedToken() {
		return mSignedToken;
	}
	
	/**
	 * Sets the signed token for a specific attribute
	 * @param token
	 */
	public void setSignedToken(String token) {
		mSignedToken = token;
	}
	
	/**
	 * Returns the stored ID for the token
	 * @return - the stored ID for the token
	 */
	public long getId() {
		return mId;
	}
	
	/**
	 * Sets the stored ID for the token. 
	 * The stored ID is mainly used for persistence.
	 * @param id - id for persistence. 
	 */
	public void setId(long id) {
		this.mId = id;
	}
	
	/**
	 * Sets the pending data for the attribute
	 * @param data - the pending data
	 * @throws UnsupportedOperationException
	 */
	public void setPendingData(String data) throws UnsupportedOperationException {
		if(this.mState.equals(Constants.ATTRIBUTE_STATE.NOT_VERIFIABLE)) {
			MIDaaS.logDebug(TAG, "Cannot set pending data for a unverifiable attribute.");
			throw new UnsupportedOperationException("Cannot set pending data for a unverifiable attribute.");
		} else {
			this.mPendingData = data;
		}
	}
	
	/**
	 * Returns the pending data for the attribute
	 * @return - pending data or null if no pending data is available. 
	 */
	public String getPendingData() {
		return mPendingData;
	}
	
	public void save() throws MIDaaSException, InvalidAttributeValueException {
		if(validateAttribute(this.mValue)) {
			boolean isSaved = AttributePersistenceCoordinator.saveAttribute(this);
			if(!isSaved) {
				MIDaaS.logDebug(TAG, "Attribute could not be saved. Seems to be an internal database error.");
				throw new MIDaaSException(MIDaaSError.DATABASE_ERROR);
			}
		} else {
			MIDaaS.logDebug(TAG, "Attribute value could not be set. Check the value you are setting.");
			throw new InvalidAttributeValueException("Attribute value could not be set. Check the value you are setting.");
		}
	}
	
	/**
	 * Returns the state of the attribute. 
	 * @return
	 * if pending data is present and signed token is absent, returns pending verification
	 * if pending data is absent and signed token is present, returns verified 
	 * if pending data is absent and signed token is absent, returns the current state. 
	 */
	public ATTRIBUTE_STATE getState() {
		if(mPendingData != null && mSignedToken == null) {
			return ATTRIBUTE_STATE.PENDING_VERIFICATION;
		} else if(mPendingData == null && mSignedToken != null) {
			return ATTRIBUTE_STATE.VERIFIED;
		} else if(mPendingData == null && mSignedToken == null) {
			return mState;
		}
		return ATTRIBUTE_STATE.UNKNOWN;
	}
	
	/**
	 * Set the verification method required by the server for 
	 * this attribute
	 * @param method a well defined verification method
	 */
	public void setVerificationMethod(String method) {
		this.mVerificationMethod = method;
	}
	
	/**
	 * Returns the verification method set for this
	 * attribute
	 * @return the verification method
	 */
	public String getVerificationMethod() {
		return mVerificationMethod;
	}
	
	/**
	 * Returns the attribute's value
	 * @return - the attribute's value.
	 */
	public T getValue() {
		return mValue;
	}

	/**
	 * Set's the attribute's value. 
	 * @param value - the attribute value
	 * @throws InvalidAttributeValueException - if the attribute is invalid
	 */
	public final void setValue(T value) throws InvalidAttributeValueException {
		if(validateAttribute(value)) {
			this.mValue = value;
		} else {
			MIDaaS.logDebug(TAG, "Attribute value could not be set. Check the value you are setting.");
			throw new InvalidAttributeValueException("Attribute value could not be set. Check the value you are setting.");
		}
	}

	/**
	 * An abstract method that validates the attribute's value. 
	 * @param value
	 * @return true if attribute was verified successfully, 
	 * 		   false otherwise. 
	 */
	protected abstract boolean validateAttribute(T value);
	
	/**
	 * Method that starts the attribute verification.
	 * @param callback - the verification callback
	 * @throws NotVerifiableException - throws if the attribute is not verifiable
	 */
	public void startVerification(InitializeVerificationCallback callback) throws UnsupportedOperationException {
		MIDaaS.logDebug(TAG, "Cannot start verification");
		throw new UnsupportedOperationException("Cannot start verification"); 
	}
	
	/**
	 * Method that completes the attribute verification after collecting a code from the user.
	 * @param code - the one-time verification code collected from the user.
	 * @param callback - the verification complete callback
	 * @throws NotVerifiableException
	 */
	public void completeVerification(String code, CompleteVerificationCallback callback) throws UnsupportedOperationException  {
		MIDaaS.logDebug(TAG, "Cannot complete verification");
		throw new UnsupportedOperationException("Cannot complete verification");
	}
	
	/**
	 * Removes an attribute from the persistence store. 
	 * @throws MIDaaSException if an error occurred while
	 * removing the attribute. 
	 */
	public void delete() throws MIDaaSException {
		AttributePersistenceCoordinator.removeAttribute(this);
	}
	
	@Override
	public String toString() {
		if(mValue!=null)
			return (mValue.toString());
		MIDaaS.logError(TAG, "Attribute value is null");
		return "";
	}
	
	/**
	 * Returns the value of the attribute in the response format as described in the 
	 * release document. 
	 * @return String or JSONObject depending whether the value is a simple (String)
	 * or complex templated object. 
	 */
	public Object getResponseTokenValue() {
		if(this.mValue != null) {
			return this.mValue.toString();
		}
		return null;
	}
}
