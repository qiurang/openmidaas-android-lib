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

import org.openmidaas.library.MIDaaS;

public class RetriableTaskController {
	
	public static interface RetriableTaskCallback {
		public void onSuccess(String response);
		
		public void onError(Throwable throwable, String response);
	}
	
	private final String TAG = "RetriableTask";
	
	private int mTriesSoFar;
	
	private RetryHandler mRetryHandler;
	
	private Task mTask;
	
	private RetriableTaskCallback mRetryCallback;
	
	protected RetriableTaskController(Task task, RetryHandler handler) {
		mTriesSoFar = 0;
		mTask = task;
		mRetryHandler = handler; 
		//mRetryHandler.setRetriableTaskCallback(callback);
	}
	
	protected void startTask() {
		try {
			do {
				mTask.execute(mRetryHandler);
				mTriesSoFar++;
				if(mRetryHandler.isTerminateRequested()) {
					break;
				}
				if (mRetryHandler.shouldRetry(mTriesSoFar)) {
					MIDaaS.logDebug(TAG, "Retry count: " + mTriesSoFar);
					MIDaaS.logDebug(TAG, "Retrying... ");
					continue;
				} else {
					mRetryHandler.getCallback().onError(new Exception("Maximum tries completed"), "");
					break;
				}
			} while(true);
		} catch(Exception e) {
			MIDaaS.logError(TAG, e.getMessage());
		}
	}
}
