package com.rvrs.domain;

public record MarketSpread(String market, String spread) {

	public static MarketSpread unknown(String market) {
		return new MarketSpread(market, "N/A");
	}
}
