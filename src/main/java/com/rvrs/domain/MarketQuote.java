package com.rvrs.domain;

import java.math.BigDecimal;

public record MarketQuote(String market, BigDecimal bid, BigDecimal ask) {
}
