package de.mobile.inventorylistindexer.indexer.vehicle;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressWarnings("PMD") // manufacturerColorName to long but i would like to have a consistent naming
@SuppressFBWarnings
public class Financing {

    public String annualPercentageRate;
    public String nominalInterestRate;
    public String typeOfNominalInterestRate;
    public String firstInstallment;
    public String monthlyInstallment;
    public String finalInstallment;
    public String paybackPeriod;
    public String netLoanAmount;
    public String grossLoanAmount;
    public String closingCosts;
    public String paymentProtectionInsurance;
    public String bank;

}
