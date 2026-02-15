package com.rvrs.persistence;

import com.rvrs.domain.MarketRanking;

import java.util.Optional;

public class InMemoryMarketRankingRepository implements MarketRankingRepository {

	MarketRanking marketRanking = null;

	@Override
	public void save(MarketRanking marketRanking) {
		this.marketRanking = marketRanking;
	}

	@Override
	public Optional<MarketRanking> getMarketRanking() {
		return Optional.ofNullable(marketRanking);
	}

	@Override
	public void clear() {
		this.marketRanking = null;
	}
}
