package com.rvrs.persistence;

import com.rvrs.domain.MarketRanking;

import java.util.Optional;

public interface MarketRankingRepository {

	void save(MarketRanking marketRanking);

	Optional<MarketRanking> getMarketRanking();

	void clear();
}
