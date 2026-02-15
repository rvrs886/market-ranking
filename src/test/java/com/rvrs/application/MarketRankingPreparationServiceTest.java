package com.rvrs.application;

import com.rvrs.TestBase;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.OrderBookDto;
import com.rvrs.client.dto.TickerDto;
import com.rvrs.domain.MarketRanking;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MarketRankingPreparationServiceTest {

	SpreadDataApiClient spreadDataApiClient = mock(SpreadDataApiClient.class);

	MarketRankingPreparationService marketRankingPreparationService =
			new MarketRankingPreparationService(spreadDataApiClient);

	@Test
	void shouldAssignMarketSpreadToGroup1() {
		//given
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

		//then
		assertThat(marketRanking.ranking().group1()).hasSize(1);
		assertThat(marketRanking.ranking().group1().get(0).market()).isEqualTo(tickerId);
		assertThat(new BigDecimal(marketRanking.ranking().group1().get(0).spread())).isLessThanOrEqualTo(BigDecimal.valueOf(2.00));
		assertThat(marketRanking.ranking().group2()).isEmpty();
		assertThat(marketRanking.ranking().group3()).isEmpty();
	}

	@Test
	void shouldAssignMarketSpreadToGroup2() {
		//given
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
		assertThat(marketRanking.ranking().group1()).isEmpty();
		assertThat(marketRanking.ranking().group2()).hasSize(1);
		assertThat(marketRanking.ranking().group2().get(0).market()).isEqualTo(tickerId);
		assertThat(new BigDecimal(marketRanking.ranking().group2().get(0).spread())).isGreaterThan(BigDecimal.valueOf(2.00));
		assertThat(marketRanking.ranking().group3()).isEmpty();
	}

	@Test
	void shouldAssignMarketSpreadToGroup3() {
		//given
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
		assertThat(marketRanking.ranking().group1()).isEmpty();
		assertThat(marketRanking.ranking().group2()).isEmpty();
		assertThat(marketRanking.ranking().group3()).hasSize(1);
		assertThat(marketRanking.ranking().group3().get(0).market()).isEqualTo(tickerId);
		assertThat(marketRanking.ranking().group3().get(0).spread()).isEqualTo("N/A");
	}

	@Test
	void shouldSortMarketsInGroupAlphabetically() {
		//given
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
		assertThat(marketRanking.ranking().group1()).hasSize(2);
		assertThat(marketRanking.ranking().group1().get(0).market()).isEqualTo(btcUsdcTickerId);
	}

}