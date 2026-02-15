package com.rvrs.domain;

import java.time.Instant;

public record MarketRanking(Instant timestamp, MarketRankingGroups ranking) {
}
