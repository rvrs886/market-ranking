package com.rvrs.application.strategy;

import com.rvrs.domain.MarketSpread;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.util.Objects.nonNull;

@Component
public class BelowTwoPercentMarketSpreadStrategy implements MarketSpreadPreparationStrategy {

	@Override
	public String getGroupName() {
		return "group2";
	}

	@Override
	public MarketSpread prepare(String market, BigDecimal spreadValue) {
		String spreadString = spreadValue.setScale(2, RoundingMode.HALF_UP).toPlainString();
		return new MarketSpread(market, spreadString);
	}

	@Override
	public boolean isApplicable(BigDecimal spreadValue) {
		return nonNull(spreadValue) && spreadValue.compareTo(new BigDecimal("2.00")) > 0;
	}
}
