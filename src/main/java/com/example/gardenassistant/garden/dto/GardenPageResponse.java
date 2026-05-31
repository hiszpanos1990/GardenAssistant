package com.example.gardenassistant.garden.dto;

import java.io.Serializable;
import java.util.List;

public record GardenPageResponse(List<GardenResponse> content,
                                 long totalElements,
                                 int totalPages,
                                 int currentPage) implements Serializable {
}
