package com.example.gardenassistant.garden.dto;

import java.util.List;

public record GeoLocationResponse(List<Coordinates> results) {
}
