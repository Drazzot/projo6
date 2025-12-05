package com.creditcardcalculatorbrionblais.model.strategy;

	import java.math.BigDecimal;
	import java.time.LocalDate;

	public interface InterestCalculator {
	    BigDecimal calculateInterest(
	            BigDecimal balance,
	            LocalDate cycleStart,
	            LocalDate cycleEnd,
	            BigDecimal apr
	    );
	}
