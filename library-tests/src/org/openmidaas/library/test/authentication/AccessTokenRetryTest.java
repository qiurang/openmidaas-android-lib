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
package org.openmidaas.library.test.authentication;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.authentication.AuthenticationManager;
import org.openmidaas.library.common.network.ConnectionManager;
import org.openmidaas.library.model.core.InitializeVerificationCallback;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;
import org.openmidaas.library.test.models.MockAttribute;
import org.openmidaas.library.test.models.MockPersistence;
import org.openmidaas.library.test.network.MockTransport;
import org.openmidaas.library.test.network.MockTransportFactory;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class AccessTokenRetryTest  extends InstrumentationTestCase {
	
	private MockAttribute mAttribute;
	private MockTransportFactory mockFactory;
	private boolean notificationSuccess = false;
	protected void setUp() throws Exception {
		MIDaaS.setContext( getInstrumentation().getContext());
		mAttribute = new MockAttribute();
		AttributePersistenceCoordinator.setPersistenceDelegate(new MockPersistence());
		AuthenticationManager.getInstance().setAccessTokenStrategy(new MockAccessTokenSuccessStrategy());
		mockFactory = new MockTransportFactory("init_attr_ver_success.json");
		mockFactory.setTransport(new MockAccessTokenTransportRequest(getInstrumentation().getContext()));
		ConnectionManager.setNetworkFactory(mockFactory);
	}
	
	@SmallTest
	public void testRetrySuccess() throws Exception {
		final CountDownLatch mLatch = new CountDownLatch(1);
		mockFactory.setFilename("access_token_reject.json");
		mAttribute.startVerification(new InitializeVerificationCallback() {

			@Override
			public void onSuccess() {
				notificationSuccess = true;
				mLatch.countDown();
				
			}

			@Override
			public void onError(MIDaaSException exception) {
				notificationSuccess = false;
				mLatch.countDown();
			}
			
		});
		mLatch.await();
		Assert.assertTrue(notificationSuccess);
	}

}
