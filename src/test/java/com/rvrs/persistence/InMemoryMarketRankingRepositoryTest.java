package com.rvrs.persistence;

import com.rvrs.TestBase;
import com.rvrs.domain.MarketRanking;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryMarketRankingRepositoryTest extends TestBase {

	InMemoryMarketRankingRepository repository = new InMemoryMarketRankingRepository();

	@Test
	void shouldSaveMarketRanking() {
		//given
		MarketRanking sampleMarketRanking = sampleMarketRanking();

		//when
		repository.save(sampleMarketRanking);

		//then
		assertThat(repository.getMarketRanking()).isPresent();
		assertThat(repository.getMarketRanking().get()).isEqualTo(sampleMarketRanking);
	}

	@Test
	void shouldReturnOptionalEmptyWhenMarketRankingIsNotPresent() {
		//when
		Optional<MarketRanking> marketRanking = repository.getMarketRanking();

		//then
		assertThat(marketRanking).isEmpty();
	}

	@Test
	void shouldClearMarketRanking() {
		//given
		MarketRanking sampleMarketRanking = sampleMarketRanking();
		repository.save(sampleMarketRanking);

		//when
		repository.clear();

		//then
		assertThat(repository.getMarketRanking()).isEmpty();
	}

}