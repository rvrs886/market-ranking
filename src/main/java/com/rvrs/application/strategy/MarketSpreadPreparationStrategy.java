package com.rvrs.application.strategy;

import com.rvrs.domain.MarketSpread;

import java.math.BigDecimal;

public interface MarketSpreadPreparationStrategy {

	String getGroupName();

	MarketSpread prepare(String market, BigDecimal spreadValue);

	boolean isApplicable(BigDecimal spreadValue);
}
