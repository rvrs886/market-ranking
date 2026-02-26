package com.rvrs.application;

import com.rvrs.application.strategy.MarketSpreadPreparationStrategy;
import com.rvrs.application.strategy.MarketSpreadPreparationStrategyRegistry;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.OrderBookDto;
import com.rvrs.domain.MarketQuote;
import com.rvrs.domain.MarketRanking;
import com.rvrs.domain.MarketSpread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MarketRankingPreparationService {

	private static final Comparator<MarketSpread> BY_MARKET = Comparator.comparing(MarketSpread::market, String.CASE_INSENSITIVE_ORDER);
	private static final Logger log = LoggerFactory.getLogger(MarketRankingPreparationService.class);
	private final SpreadDataApiClient spreadDataApiClient;
	private final MarketSpreadPreparationStrategyRegistry marketSpreadPreparationStrategyRegistry;
	private final ExecutorService executorService = Executors.newFixedThreadPool(4);

	public MarketRankingPreparationService(SpreadDataApiClient spreadDataApiClient,
	                                       MarketSpreadPreparationStrategyRegistry marketSpreadPreparationStrategyRegistry) {
		this.spreadDataApiClient = spreadDataApiClient;
		this.marketSpreadPreparationStrategyRegistry = marketSpreadPreparationStrategyRegistry;
	}

	public MarketRanking prepare(List<String> tickerIds) {
		Instant timestamp = Instant.now();
		Map<String, List<MarketSpread>> groupWithSpreads = new ConcurrentHashMap<>();

		List<CompletableFuture<Void>> threads = tickerIds.stream()
				.map(tickerId -> CompletableFuture.runAsync(() -> {
					OrderBookDto orderBookDto = spreadDataApiClient.getOrderBook(tickerId);
					MarketQuote marketQuote = new MarketQuote(
							orderBookDto.tickerId(),
							orderBookDto.bestBid().orElse(null),
							orderBookDto.bestAsk().orElse(null)
					);

					BigDecimal spread = SpreadCalculator.calculateSpread(marketQuote.bid(), marketQuote.ask());

					MarketSpreadPreparationStrategy strategy = marketSpreadPreparationStrategyRegistry.getStrategyFor(spread);
					String groupName = strategy.getGroupName();

					MarketSpread marketSpread = strategy.prepare(marketQuote.market(), spread);

					groupWithSpreads
							.computeIfAbsent(groupName, _ -> Collections.synchronizedList(new ArrayList<>()))
							.add(marketSpread);
				}, executorService))
				.toList();

		CompletableFuture.allOf(threads.toArray(CompletableFuture[]::new)).join();

		Map<String, List<MarketSpread>> sortedMap = transformToNaturalSortedMap(groupWithSpreads);

		for (Map.Entry<String, List<MarketSpread>> entry : sortedMap.entrySet()) {
			entry.getValue().sort(BY_MARKET);
		}

		return new MarketRanking(timestamp, sortedMap);
	}

	private Map<String, List<MarketSpread>> transformToNaturalSortedMap(Map<String, List<MarketSpread>> map) {
		return new TreeMap<>(map);
	}
}
