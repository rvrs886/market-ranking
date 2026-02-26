package com.rvrs.application;

import com.rvrs.application.exception.MarketRankingNotPresentException;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.TickerDto;
import com.rvrs.domain.MarketRanking;
import com.rvrs.persistence.MarketRankingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class MarketRankingService {

	private static final Logger log = LoggerFactory.getLogger(MarketRankingService.class);
	private final MarketRankingRepository marketRankingRepository;
	private final SpreadDataApiClient spreadDataApiClient;
	private final MarketRankingPreparationService marketRankingPreparationService;
	private final ExecutorService executorService = Executors.newSingleThreadExecutor();

	private final AtomicReference<CompletableFuture<Void>> running = new AtomicReference<>();

	public MarketRankingService(MarketRankingRepository marketRankingRepository,
	                            SpreadDataApiClient spreadDataApiClient,
	                            MarketRankingPreparationService marketRankingPreparationService) {
		this.marketRankingRepository = marketRankingRepository;
		this.spreadDataApiClient = spreadDataApiClient;
		this.marketRankingPreparationService = marketRankingPreparationService;
	}

	public CompletableFuture<Void> calculateMarketRanking() {
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			log.info("Spread ranking calculation started...");

			List<TickerDto> tickers = spreadDataApiClient.getTickers();
			List<String> tickerIds = tickers.stream()
					.map(TickerDto::tickerId)
					.toList();

			MarketRanking marketRanking = marketRankingPreparationService.prepare(tickerIds);

			marketRankingRepository.save(marketRanking);

			log.info("Spread ranking calculation finished.");
		}, executorService).whenComplete((result, throwable) -> {
			running.compareAndSet(running.get(), null);

			if (throwable != null) {
				log.error("Spread ranking calculation failed.", throwable);
			}
		});

		if (!running.compareAndSet(null, future)) {
			throw new IllegalStateException("Spread ranking calculation already in progress.");
		}

		return future;
	}

	public MarketRanking getMarketRanking() {
		CompletableFuture<Void> future = running.get();
		if (future != null && !future.isDone()) {
			throw new MarketRankingNotPresentException("Spread ranking not found, calculation in progress.");
		}

		return marketRankingRepository.getMarketRanking()
				.orElseThrow(() -> new MarketRankingNotPresentException("Spread ranking not found, please calculate it first."));
	}
}
