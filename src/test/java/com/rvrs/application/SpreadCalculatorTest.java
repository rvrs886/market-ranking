package com.rvrs.application;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class SpreadCalculatorTest {

	@Test
	void shouldProperlyCalculateSpread() {
		//given
		BigDecimal ask = BigDecimal.valueOf(4.5997);
		BigDecimal bid = BigDecimal.valueOf(4.2610);

		//when
		BigDecimal spread = SpreadCalculator.calculateSpread(bid, ask);

		//then
		assertThat(spread).isNotNull();
		assertThat(spread).isEqualTo(BigDecimal.valueOf(7.64));
	}

	@Test
	void shouldReturnEmptyResultWhenAskIsNull() {
		//given
		BigDecimal ask = null;
		BigDecimal bid = BigDecimal.valueOf(4.2610);

		//when
		BigDecimal spread = SpreadCalculator.calculateSpread(ask, bid);

		//then
		assertThat(spread).isNull();
	}

	@Test
	void shouldReturnEmptyResultWhenBidIsNull() {
		//given
		BigDecimal ask = BigDecimal.valueOf(4.2610);
		BigDecimal bid = null;

		//when
		BigDecimal spread = SpreadCalculator.calculateSpread(ask, bid);

		//then
		assertThat(spread).isNull();
	}

	@Test
	void shouldReturnEmptyResultWhenAskAndBidAreEmpty() {
		//given
		BigDecimal ask = null;
		BigDecimal bid = null;

		//when
		BigDecimal spread = SpreadCalculator.calculateSpread(ask, bid);

		//then
		assertThat(spread).isNull();
	}

	@Test
	void shouldReturnEmptyResultWhenAskAndBidAreZero() {
		//given
		BigDecimal ask = BigDecimal.ZERO;
		BigDecimal bid = BigDecimal.ZERO;

		//when
		BigDecimal spread = SpreadCalculator.calculateSpread(ask, bid);

		//then
		assertThat(spread).isNull();
	}
}