package com.rvrs.client.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderBookDtoTest {

	@Test
	void shouldReturnFirstAskAndBid() {
		//given
		String tickerId = "JASMY_USDC";
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

		//when
		Optional<BigDecimal> bestAsk = orderBookDto.bestAsk();
		Optional<BigDecimal> bestBid = orderBookDto.bestBid();

		//then
		assertThat(bestAsk).isPresent();
		assertThat(bestAsk.get()).isEqualTo(BigDecimal.valueOf(0.00634));
		assertThat(bestBid).isPresent();
		assertThat(bestBid.get()).isEqualTo(BigDecimal.valueOf(0.00602));
	}

	@Test
	void shouldReturnEmptyBestBidAndAskWhenValueAreEmpty() {
		//given
		String tickerId = "JASMY_USDC";
		OrderBookDto orderBookDto = new OrderBookDto(
				Instant.now().toEpochMilli(),
				List.of(),
				List.of(),
				tickerId
		);

		//when
		Optional<BigDecimal> bestAsk = orderBookDto.bestAsk();
		Optional<BigDecimal> bestBid = orderBookDto.bestBid();

		//then
		assertThat(bestAsk).isEmpty();
		assertThat(bestBid).isEmpty();
	}

}