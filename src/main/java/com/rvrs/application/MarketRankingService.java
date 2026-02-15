package com.rvrs.application;

import com.rvrs.application.exception.MarketRankingNotPresentException;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.TickerDto;
import com.rvrs.domain.MarketRanking;
import com.rvrs.persistence.MarketRankingRepository;

import java.util.List;

public class MarketRankingService {

	private final MarketRankingRepository marketRankingRepository;
	private final SpreadDataApiClient spreadDataApiClient;
	private final MarketRankingPreparationService marketRankingPreparationService;

	public MarketRankingService(MarketRankingRepository marketRankingRepository,
	                            SpreadDataApiClient spreadDataApiClient,
	                            MarketRankingPreparationService marketRankingPreparationService) {
		this.marketRankingRepository = marketRankingRepository;
		this.spreadDataApiClient = spreadDataApiClient;
		this.marketRankingPreparationService = marketRankingPreparationService;
	}

	public void calculateMarketRanking() {
		List<TickerDto> tickers = spreadDataApiClient.getTickers();
		List<String> tickerIds = tickers.stream()
				.map(TickerDto::tickerId)
				.toList();
		MarketRanking marketRanking = marketRankingPreparationService.prepare(tickerIds);
		marketRankingRepository.save(marketRanking);
	}

	public MarketRanking getMarketRanking() {
		return marketRankingRepository.getMarketRanking()
				.orElseThrow(() -> new MarketRankingNotPresentException("Spread ranking not found, please calculate it first."));
	}
}
