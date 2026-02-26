package com.rvrs.domain;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record MarketRanking(Instant timestamp, Map<String, List<MarketSpread>> ranking) {
}
