package com.rvrs.client;

import com.rvrs.client.dto.OrderBookDto;
import com.rvrs.client.dto.TickerDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class SpreadDataApiClient {

	private final RestClient restClient;

	public SpreadDataApiClient(RestClient restClient) {
		this.restClient = restClient;
	}

	public List<TickerDto> getTickers() {
		return restClient.get()
				.uri("/api/market/pairs")
				.retrieve()
				.body(new ParameterizedTypeReference<>(){});
	}

	public OrderBookDto getOrderBook(String tickerId) {
		return restClient.get()
				.uri("/api/market/orderbook/{tickerId}", tickerId)
				.retrieve()
				.body(OrderBookDto.class);
	}
}
