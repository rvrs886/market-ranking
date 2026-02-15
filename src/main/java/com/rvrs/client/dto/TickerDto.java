package com.rvrs.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TickerDto(@JsonProperty("ticker_id") String tickerId,
                        String base,
                        String target) {
}
