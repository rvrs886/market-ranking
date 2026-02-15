package com.rvrs;

import com.rvrs.domain.MarketRanking;
import com.rvrs.domain.MarketRankingGroups;
import com.rvrs.domain.MarketSpread;

import java.time.Instant;
import java.util.List;

public class TestBase {

	protected MarketRanking sampleMarketRanking() {
		MarketRankingGroups marketRankingGroups = new MarketRankingGroups(
				List.of(new MarketSpread("BTC_USDC", "1.99")),
				List.of(new MarketSpread("ALGO_USDC", "2.99")),
				List.of(new MarketSpread("ETH_USDC", "N/A"))
		);
		return new MarketRanking(Instant.now(), marketRankingGroups);
	}
}
