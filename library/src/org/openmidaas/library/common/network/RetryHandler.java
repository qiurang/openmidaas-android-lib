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

import org.openmidaas.library.common.network.RetriableTaskController.RetriableTaskCallback;

public abstract class RetryHandler {
	
	private boolean isTerminateRequested;
	
	private RetriableTaskCallback mRetriableTaskCallback;
	
	protected RetryHandler(){}
	
	public void terminate() {
		this.isTerminateRequested = true;
	}

	public boolean isTerminateRequested() {
		return isTerminateRequested;
	}
	
	public void setRetriableTaskCallback(RetriableTaskCallback callback) {
		this.mRetriableTaskCallback = callback;
	}
	
	public RetriableTaskCallback getCallback() {
		return mRetriableTaskCallback;
	}
	
	public abstract void onSuccess(String response);
	
	public abstract void onError(Throwable throwable, String response);
	
	public abstract boolean shouldRetry(int currentRetryCount);
}
