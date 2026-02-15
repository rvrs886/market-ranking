package com.rvrs.application;

import com.rvrs.application.exception.MarketRankingNotPresentException;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.client.dto.TickerDto;
import com.rvrs.domain.MarketRanking;
import com.rvrs.domain.MarketRankingGroups;
import com.rvrs.persistence.MarketRankingRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MarketRankingServiceTest {

	private SpreadDataApiClient spreadDataApiClient = mock(SpreadDataApiClient.class);
	private MarketRankingPreparationService marketRankingPreparationService = mock(MarketRankingPreparationService.class);
	private MarketRankingRepository repository = mock(MarketRankingRepository.class);

	private MarketRankingService marketRankingService = new MarketRankingService(
			repository,
			spreadDataApiClient,
			marketRankingPreparationService
	);

	@Test
	void shouldThrowMarketRankingNotPresentExceptionWhenRankingIsNotGenerated() {
		//when & then
		Assertions.assertThatThrownBy(() -> marketRankingService.getMarketRanking())
				.isInstanceOf(MarketRankingNotPresentException.class)
				.hasMessageContaining("Spread ranking not found, please calculate it first.");

		verify(repository, times(1)).getMarketRanking();
	}

	@Test
	void shouldCalculateMarketRanking() {
		//given
		List<TickerDto> tickers = List.of(
				new TickerDto("BTC_USD", "BTC", "USD"),
				new TickerDto("ETH_USDC", "ETH", "USDC")
		);

		when(spreadDataApiClient.getTickers()).thenReturn(tickers);

		MarketRanking prepared = new MarketRanking(
				Instant.parse("2026-02-15T10:00:00Z"),
				new MarketRankingGroups(List.of(), List.of(), List.of())
		);

		when(marketRankingPreparationService.prepare(List.of("BTC_USD", "ETH_USDC")))
				.thenReturn(prepared);

		//when
		marketRankingService.calculateMarketRanking();

		//then
		verify(spreadDataApiClient, times(1)).getTickers();
		verify(marketRankingPreparationService, times(1))
				.prepare(List.of("BTC_USD", "ETH_USDC"));
		ArgumentCaptor<MarketRanking> captor = ArgumentCaptor.forClass(MarketRanking.class);
		verify(repository, times(1)).save(captor.capture());
	}

	@Test
	void shouldReturnMarketRankingWhenPresent() {
		//given
		MarketRanking ranking = new MarketRanking(
				Instant.parse("2026-02-15T11:00:00Z"),
				new MarketRankingGroups(List.of(), List.of(), List.of())
		);

		when(repository.getMarketRanking()).thenReturn(Optional.of(ranking));

		//when
		MarketRanking result = marketRankingService.getMarketRanking();

		//then
		assertThat(ranking).isEqualTo(result);
		verify(repository, times(1)).getMarketRanking();
		verifyNoMoreInteractions(repository);
	}



}