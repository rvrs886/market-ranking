package com.rvrs.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class SpreadCalculator {

	public static Optional<BigDecimal> calculateSpread(BigDecimal bid, BigDecimal ask) {
		if (ask == null || bid == null) return Optional.empty();

		BigDecimal diff = ask.subtract(bid);
		BigDecimal avg = ask.add(bid).multiply(new BigDecimal("0.5"));

		if (avg.signum() == 0) return Optional.empty();

		BigDecimal spread = diff
				.divide(avg, 10, RoundingMode.HALF_UP)
				.multiply(new BigDecimal("100"));

		return Optional.of(spread.setScale(2, RoundingMode.HALF_UP));
	}
}
