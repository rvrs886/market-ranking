package com.rvrs.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

public record OrderBookDto(long timestamp,
                           List<List<String>> bids,
                           List<List<String>> asks,
                           @JsonProperty("ticker_id") String tickerId) {

	public Optional<BigDecimal> bestBid() {
		return bestPrice(bids);
	}

	public Optional<BigDecimal> bestAsk() {
		return bestPrice(asks);
	}

	private Optional<BigDecimal> bestPrice(List<List<String>> levels) {
		if (isNull(levels) || levels.isEmpty()) {
			return Optional.empty();
		}
		List<String> first = levels.getFirst();

		if (isNull(first) || first.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(new BigDecimal(first.getFirst()));
	}
}
