package com.rvrs.application;

import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.OrderBookDto;
import com.rvrs.domain.MarketQuote;
import com.rvrs.domain.MarketRanking;
import com.rvrs.domain.MarketRankingGroups;
import com.rvrs.domain.MarketSpread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.rvrs.domain.MarketSpread.unknown;

public class MarketRankingPreparationService {

	private static final Comparator<MarketSpread> BY_MARKET = Comparator.comparing(MarketSpread::market, String.CASE_INSENSITIVE_ORDER);
	private static final Logger log = LoggerFactory.getLogger(MarketRankingPreparationService.class);
	private final SpreadDataApiClient spreadDataApiClient;

	public MarketRankingPreparationService(SpreadDataApiClient spreadDataApiClient) {
		this.spreadDataApiClient = spreadDataApiClient;
	}

	public MarketRanking prepare(List<String> tickerIds) {
		Instant timestamp = Instant.now();

		int parallelism = Math.min(20, Math.max(4, tickerIds.size()));

		try (ExecutorService executorService = Executors.newFixedThreadPool(parallelism)) {
			List<CompletableFuture<MarketClassification>> futures = tickerIds.stream()
					.map(tickerId -> CompletableFuture
							.supplyAsync(() -> classifyMarket(tickerId, spreadDataApiClient), executorService)
							.exceptionally(ex -> {
								log.error("Error while classifying, assigning tickerId = {} as unknown ", tickerId, ex);
								return MarketClassification.toGroup(3, unknown(tickerId));
							}))
					.toList();

			List<MarketClassification> results = futures.stream()
					.map(CompletableFuture::join)
					.toList();

			List<MarketSpread> group1 = sorted(results, 1);
			List<MarketSpread> group2 = sorted(results, 2);
			List<MarketSpread> group3 = sorted(results, 3);

			return new MarketRanking(timestamp, new MarketRankingGroups(group1, group2, group3));
		}
	}

	private MarketClassification classifyMarket(String tickerId, SpreadDataApiClient client) {
		OrderBookDto orderBookDto = client.getOrderBook(tickerId);

		MarketQuote marketQuote = new MarketQuote(
				orderBookDto.tickerId(),
				orderBookDto.bestBid().orElse(null),
				orderBookDto.bestAsk().orElse(null)
		);

		Optional<BigDecimal> spreadOpt = SpreadCalculator.calculateSpread(marketQuote.bid(), marketQuote.ask());

		if (spreadOpt.isEmpty()) {
			return MarketClassification.toGroup(3, unknown(marketQuote.market()));
		}

		BigDecimal spreadValue = spreadOpt.get();
		String spreadString = spreadValue.setScale(2, RoundingMode.HALF_UP).toPlainString();

		if (spreadValue.compareTo(new BigDecimal("2.00")) <= 0) {
			return MarketClassification.toGroup(1, new MarketSpread(marketQuote.market(), spreadString));
		} else {
			return MarketClassification.toGroup(2, new MarketSpread(marketQuote.market(), spreadString));
		}
	}

	private List<MarketSpread> sorted(List<MarketClassification> marketClassifications, int group) {
		return marketClassifications.stream()
				.filter(marketClassification -> marketClassification.group == group)
				.map(marketClassification -> marketClassification.marketSpread)
				.sorted(BY_MARKET)
				.toList();
	}

	private record MarketClassification(int group, MarketSpread marketSpread) {

		static MarketClassification toGroup(int group, MarketSpread ms) {
			return new MarketClassification(group, ms);
		}
	}
}
