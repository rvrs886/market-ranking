package com.rvrs.application;

import com.rvrs.application.strategy.AboveTwoPercentMarketSpreadStrategy;
import com.rvrs.application.strategy.BelowTwoPercentMarketSpreadStrategy;
import com.rvrs.application.strategy.MarketSpreadPreparationStrategyRegistry;
import com.rvrs.application.strategy.UnknownMarketSpreadStrategy;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.OrderBookDto;
import com.rvrs.client.dto.TickerDto;
import com.rvrs.domain.MarketRanking;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarketRankingPreparationServiceTest {

	SpreadDataApiClient spreadDataApiClient = mock(SpreadDataApiClient.class);
	MarketSpreadPreparationStrategyRegistry marketSpreadPreparationStrategyRegistry = new MarketSpreadPreparationStrategyRegistry(
			List.of(
					new AboveTwoPercentMarketSpreadStrategy(),
					new BelowTwoPercentMarketSpreadStrategy(),
					new UnknownMarketSpreadStrategy()
			)
	);

	MarketRankingPreparationService marketRankingPreparationService =
			new MarketRankingPreparationService(spreadDataApiClient, marketSpreadPreparationStrategyRegistry);

	@Test
	void shouldAssignMarketSpreadToGroup1() {
		//given
		String group1 = "group1";
		String group2 = "group2";
		String group3 = "group3";
		String tickerId = "JASMY_USDC";
		TickerDto tickerDto = new TickerDto(tickerId, "JASMY", "USDC");
		OrderBookDto orderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(
						List.of("100", "101"),
						List.of("102", "103")
				),
				List.of(
						List.of("101", "105"),
						List.of("104", "106")
				),
				tickerId
		);
		when(spreadDataApiClient.getTickers()).thenReturn(
				List.of(tickerDto)
		);
		when(spreadDataApiClient.getOrderBook(eq(tickerId))).thenReturn(orderBookDto);

		//when
		MarketRanking marketRanking = marketRankingPreparationService.prepare(List.of(tickerId));

		System.out.println();
		//then
		assertThat(marketRanking.ranking().get(group1).size()).isEqualTo(1);
		assertThat(marketRanking.ranking().get(group1).get(0).market()).isEqualTo(tickerId);
		assertThat(new BigDecimal(marketRanking.ranking().get(group1).get(0).spread())).isLessThanOrEqualTo(BigDecimal.valueOf(2.00));
		assertThat(marketRanking.ranking().get(group2).isEmpty());
		assertThat(marketRanking.ranking().get(group3).isEmpty());
	}

	@Test
	void shouldAssignMarketSpreadToGroup2() {
		//given
		String group1 = "group1";
		String group2 = "group2";
		String group3 = "group3";
		String tickerId = "JASMY_USDC";
		TickerDto tickerDto = new TickerDto(tickerId, "JASMY", "USDC");
		OrderBookDto orderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(
						List.of("0.00602", "54030.64996743"),
						List.of("0.00578", "22935.33529243")
				),
				List.of(
						List.of("0.00634", "11657.57200215"),
						List.of("0.00659", "13937.41801197")
				),
				tickerId
		);
		when(spreadDataApiClient.getTickers()).thenReturn(
				List.of(tickerDto)
		);
		when(spreadDataApiClient.getOrderBook(eq(tickerId))).thenReturn(orderBookDto);

		//when
		MarketRanking marketRanking = marketRankingPreparationService.prepare(List.of(tickerId));

		//then
		assertThat(marketRanking.ranking().get(group1).isEmpty());
		assertThat(marketRanking.ranking().get(group2).size()).isEqualTo(1);
		assertThat(marketRanking.ranking().get(group2).get(0).market()).isEqualTo(tickerId);
		assertThat(new BigDecimal(marketRanking.ranking().get(group2).get(0).spread())).isGreaterThan(BigDecimal.valueOf(2.00));
		assertThat(marketRanking.ranking().get(group3).size()).isEqualTo(0);
	}

	@Test
	void shouldAssignMarketSpreadToGroup3() {
		//given
		String group1 = "group1";
		String group2 = "group2";
		String group3 = "group3";
		String tickerId = "JASMY_USDC";
		TickerDto tickerDto = new TickerDto(tickerId, "JASMY", "USDC");
		OrderBookDto orderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(
						List.of("0.00", "0.00"),
						List.of("0.00578", "22935.33529243")
				),
				List.of(
						List.of("0.00", "11657.57200215"),
						List.of("0.00659", "13937.41801197")
				),
				tickerId
		);
		when(spreadDataApiClient.getTickers()).thenReturn(
				List.of(tickerDto)
		);
		when(spreadDataApiClient.getOrderBook(eq(tickerId))).thenReturn(orderBookDto);

		//when
		MarketRanking marketRanking = marketRankingPreparationService.prepare(List.of(tickerId));

		//then
		assertThat(marketRanking.ranking().get(group1).isEmpty());
		assertThat(marketRanking.ranking().get(group2).isEmpty());
		assertThat(marketRanking.ranking().get(group3).size()).isEqualTo(1);
		assertThat(marketRanking.ranking().get(group3).get(0).market()).isEqualTo(tickerId);
		assertThat(marketRanking.ranking().get(group3).get(0).spread()).isEqualTo("N/A");
	}

	@Test
	void shouldSortMarketsInGroupAlphabetically() {
		//given
		String group1 = "group1";
		String jasmyUsdcTickerId = "JASMY_USDC";
		String btcUsdcTickerId = "BTC_USDC";
		TickerDto jasmyUsdcTickerDto = new TickerDto(jasmyUsdcTickerId, "JASMY", "USDC");
		TickerDto btcUsdcTickerDto = new TickerDto(btcUsdcTickerId, "BTC", "USDC");
		OrderBookDto jasmyUsdcOrderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(
						List.of("100", "101"),
						List.of("102", "103")
				),
				List.of(
						List.of("101", "105"),
						List.of("104", "106")
				),
				jasmyUsdcTickerId
		);
		OrderBookDto btcUsdcOrderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(
						List.of("100", "101"),
						List.of("102", "103")
				),
				List.of(
						List.of("101", "105"),
						List.of("104", "106")
				),
				btcUsdcTickerId
		);
		when(spreadDataApiClient.getTickers()).thenReturn(
				List.of(jasmyUsdcTickerDto, btcUsdcTickerDto)
		);
		when(spreadDataApiClient.getOrderBook(eq(jasmyUsdcTickerId))).thenReturn(jasmyUsdcOrderBookDto);
		when(spreadDataApiClient.getOrderBook(eq(btcUsdcTickerId))).thenReturn(btcUsdcOrderBookDto);

		//when
		MarketRanking marketRanking = marketRankingPreparationService.prepare(List.of(btcUsdcTickerId, jasmyUsdcTickerId));

		//then
		assertThat(marketRanking.ranking().get(group1).size()).isEqualTo(2);
		assertThat(marketRanking.ranking().get(group1).get(0).market()).isEqualTo(btcUsdcTickerId);
	}
}