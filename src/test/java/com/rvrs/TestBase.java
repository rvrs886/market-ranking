package com.rvrs;

import com.rvrs.domain.MarketRanking;
import com.rvrs.domain.MarketSpread;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestBase {

	protected MarketRanking sampleMarketRanking() {
		Map<String, List<MarketSpread>> ranking = Map.of(
				"group1", List.of(new MarketSpread("BTC_USDC", "1.99")),
				"group2", List.of(new MarketSpread("ALGO_USDC", "2.99")),
				"group3", List.of(new MarketSpread("ETH_USDC", "N/A"))
		);
		return new MarketRanking(Instant.now(), ranking);
	}
}
