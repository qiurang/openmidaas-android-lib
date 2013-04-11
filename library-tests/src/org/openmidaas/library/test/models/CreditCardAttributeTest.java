package org.openmidaas.library.test.models;

import org.junit.Assert;
import org.openmidaas.library.MIDaaS;
import org.openmidaas.library.model.AttributeFactory;
import org.openmidaas.library.model.CreditCardAttribute;
import org.openmidaas.library.model.CreditCardValue;
import org.openmidaas.library.model.InvalidAttributeValueException;
import org.openmidaas.library.model.core.MIDaaSException;
import org.openmidaas.library.persistence.AttributePersistenceCoordinator;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class CreditCardAttributeTest extends InstrumentationTestCase {
	
	private final String VALID_CARD_NUMBER = "4485227712981401";
	private final String INVALID_CARD_NUMBER = "1234";
	private final short VALID_EXPIRY_MONTH = 01;
	private final short INVALID_EXPIRY_MONTH = 13;
	private final short VALID_EXPIRY_YEAR = 14;
	private final short INVALID_EXPIRY_YEAR = 02;
	private final String CARD_HOLDER_NAME = "Rob Smith";
	private CreditCardValue mValue = new CreditCardValue(VALID_CARD_NUMBER, VALID_EXPIRY_MONTH, VALID_EXPIRY_YEAR, CARD_HOLDER_NAME);
	private CreditCardAttribute creditCardAttribute;
	protected void setUp() throws Exception {
		MIDaaS.setContext(getInstrumentation().getContext());
		AttributePersistenceCoordinator.setPersistenceDelegate(new MockPersistence());
	}
	
	@SmallTest
	public void testValidCreditCardValue() {
		try {
			createAttribute();
			Assert.assertEquals(VALID_CARD_NUMBER, creditCardAttribute.getValue().getCreditCardNumber());
			Assert.assertEquals(VALID_EXPIRY_MONTH, creditCardAttribute.getValue().getExpiryMonth());
			Assert.assertEquals(VALID_EXPIRY_YEAR, creditCardAttribute.getValue().getExpiryYear());
			Assert.assertEquals(VALID_EXPIRY_MONTH, creditCardAttribute.getValue().getExpiryMonth());
			Assert.assertEquals(CARD_HOLDER_NAME, creditCardAttribute.getValue().getHolderName());
		} catch (InvalidAttributeValueException e) {
			Assert.fail();
		} catch (MIDaaSException e) {
			Assert.fail();
		}
	}

	@SmallTest
	public void testInvalidCreditCardNumber() {
		mValue.setCreditCardNumber(INVALID_CARD_NUMBER);
		try {
			createAttribute();
		} catch (InvalidAttributeValueException e) {
		} catch (MIDaaSException e) {
			Assert.fail();
		}
	}
	
	@SmallTest
	public void testInvalidMonthOnCard() {
		mValue.setExpiryMonth(INVALID_EXPIRY_MONTH);
		try {
			createAttribute();
		} catch (InvalidAttributeValueException e) {
		} catch (MIDaaSException e) {
			Assert.fail();
		}
	}
	
	@SmallTest
	public void testInvalidYearOnCard() {
		mValue.setExpiryYear(INVALID_EXPIRY_YEAR);
		try {
			createAttribute();
		} catch (InvalidAttributeValueException e) {
		} catch (MIDaaSException e) {
			Assert.fail();
		}
	}
	
	@SmallTest
	public void testWithAllNullNumber() {
		mValue.setCreditCardNumber(null);
		try {
			createAttribute();
		} catch (InvalidAttributeValueException e) {
		} catch (MIDaaSException e) {
			Assert.fail();
		}
	}
	
	private void createAttribute() throws InvalidAttributeValueException, MIDaaSException {
		creditCardAttribute = AttributeFactory.getCreditCardAttributeFactory().createAttributeWithValue(mValue);
	}
}
