package com.rvrs.domain;

import java.util.List;

public record MarketRankingGroups(List<MarketSpread> group1,
                                  List<MarketSpread> group2,
                                  List<MarketSpread> group3) {
}
