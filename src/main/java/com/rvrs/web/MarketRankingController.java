package com.rvrs.web;

import com.rvrs.application.MarketRankingService;
import com.rvrs.application.exception.MarketRankingNotPresentException;
import com.rvrs.domain.MarketRanking;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/spread")
public class MarketRankingController {

	private final MarketRankingService marketRankingService;

	public MarketRankingController(MarketRankingService marketRankingService) {
		this.marketRankingService = marketRankingService;
	}

	@GetMapping("/ranking")
	public MarketRanking getMarketRanking() {
		return marketRankingService.getMarketRanking();
	}

	@PostMapping("/calculate")
	public void calculateMarketRanking() {
		marketRankingService.calculateMarketRanking();
	}

	@ExceptionHandler(MarketRankingNotPresentException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleMarketRankingNotPresentException(MarketRankingNotPresentException ex) {
		return ex.getMessage();
	}
}
