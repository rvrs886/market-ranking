package com.rvrs.application.strategy;

import com.rvrs.domain.MarketSpread;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.rvrs.domain.MarketSpread.unknown;
import static java.util.Objects.isNull;

@Component
public class UnknownMarketSpreadStrategy implements MarketSpreadPreparationStrategy {

	@Override
	public String getGroupName() {
		return "group3";
	}

	@Override
	public MarketSpread prepare(String market, BigDecimal spreadValue) {
		return unknown(market);
	}

	@Override
	public boolean isApplicable(BigDecimal spreadValue) {
		return isNull(spreadValue);
	}
}
