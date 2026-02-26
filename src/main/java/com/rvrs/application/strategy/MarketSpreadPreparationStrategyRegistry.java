package com.rvrs.application.strategy;

import java.math.BigDecimal;
import java.util.List;

public class MarketSpreadPreparationStrategyRegistry {

	private final List<MarketSpreadPreparationStrategy> strategies;

	public MarketSpreadPreparationStrategyRegistry(List<MarketSpreadPreparationStrategy> strategies) {
		this.strategies = strategies;
	}

	public MarketSpreadPreparationStrategy getStrategyFor(BigDecimal spreadValue) {
		return strategies.stream()
				.filter(strategy -> strategy.isApplicable(spreadValue))
				.findFirst()
				.orElseThrow();
	}
}
