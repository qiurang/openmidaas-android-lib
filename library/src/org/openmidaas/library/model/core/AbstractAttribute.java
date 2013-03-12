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

import org.openmidaas.library.model.InvalidAttributeValueException;

/**
 * Abstract class that defines an attribute
 * @param <T>
 */

public abstract class AbstractAttribute<T> {
	
	protected InitializeAttributeVerificationDelegate mInitVerificationDelegate = null;
	
	protected CompleteAttributeVerificationDelegate mCompleteVerificationDelegate = null;
	
	protected String mName;
	
	protected T mValue;
	
	private String mLabel;
	
	protected boolean mIsVerifiable = false;
	
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
	public final  void setValue(T value) throws InvalidAttributeValueException {
		if(validateAttribute(value)) {
			this.mValue = value;
		} else {
			throw new InvalidAttributeValueException();
		}
	}
	
	/**
	 * Returns true if the attribute is verifiable, false otherwise.
	 * @return true if attribute is verifiable, false otherwise. 
	 */
	public boolean isVerifiable() {
		return mIsVerifiable;
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
		throw new UnsupportedOperationException("Cannot start verification"); 
	}
	
	/**
	 * Method that completes the attribute verification after collecting a code from the user.
	 * @param code - the one-time verification code collected from the user.
	 * @param callback - the verification complete callback
	 * @throws NotVerifiableException
	 */
	public void completeVerification(String code, CompleteVerificationCallback callback) throws UnsupportedOperationException  {
		throw new UnsupportedOperationException("Cannot complete verification");
	}
}
