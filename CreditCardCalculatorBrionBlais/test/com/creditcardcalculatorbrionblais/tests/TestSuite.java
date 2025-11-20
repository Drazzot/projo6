package com.creditcardcalculatorbrionblais.tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@SelectPackages({"com.creditcardcalculatorbrionblais.app","com.creditcardcalculatorbrionblais.config",
    "com.creditcardcalculatorbrionblais.model","com.creditcardcalculatorbrionblais.view",
    "com.creditcardcalculatorbrionblais.controller","com.creditcardcalculatorbrionblais.account"})
@SelectClasses({AccountSimulatorTest.class, FeeScheduleTest.class, MainControllerTest.class,
    RewardPolicyTest.class, TransactionTest.class, PaymentStrategiesTest.class, TransactionReaderTest.class})

@Suite
public class TestSuite {

}
