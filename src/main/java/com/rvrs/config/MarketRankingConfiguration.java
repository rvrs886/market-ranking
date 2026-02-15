package com.rvrs.config;

import com.rvrs.application.MarketRankingPreparationService;
import com.rvrs.application.MarketRankingService;
import com.rvrs.client.SpreadDataApiClient;
import com.rvrs.persistence.InMemoryMarketRankingRepository;
import com.rvrs.persistence.MarketRankingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class MarketRankingConfiguration {

	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	@Bean
	public RestClient restClient(@Value("${external.api.baseUrl}") String baseUrl,
	                             @Value("${restClient.readTimeout:10000}") String readTimeout,
	                             @Value("${restClient.connectionRequestTimeout:5000}") String connectionRequestTimeout,
	                             RestClient.Builder builder) {
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setConnectionRequestTimeout(Integer.parseInt(connectionRequestTimeout));
		requestFactory.setReadTimeout(Integer.parseInt(readTimeout));

		return builder
				.baseUrl(baseUrl)
				.requestFactory(requestFactory)
				.build();
	}

	@Bean
	public SpreadDataApiClient spreadDataApiClient(RestClient restClient) {
		return new SpreadDataApiClient(restClient);
	}

	@Bean
	MarketRankingRepository marketRankingRepository() {
		return new InMemoryMarketRankingRepository();
	}

	@Bean
	MarketRankingPreparationService marketRankingPreparationService(SpreadDataApiClient spreadDataApiClient) {
		return new MarketRankingPreparationService(spreadDataApiClient);
	}

	@Bean
	MarketRankingService spreadDataService(MarketRankingRepository marketRankingRepository,
	                                       SpreadDataApiClient spreadDataApiClient,
	                                       MarketRankingPreparationService marketRankingPreparationService) {
		return new MarketRankingService(marketRankingRepository, spreadDataApiClient, marketRankingPreparationService);
	}

}