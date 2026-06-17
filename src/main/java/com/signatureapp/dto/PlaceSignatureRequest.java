package com.signatureapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class PlaceSignatureRequest {

    @NotNull(message = "Document ID is required")
    private Long documentId;

    @NotNull(message = "Page number is required")
    @Positive(message = "Page number must be positive")
    private Integer pageNumber;

    @JsonProperty("xCoordinate")
    @NotNull(message = "X coordinate is required")
    @PositiveOrZero(message = "X coordinate cannot be negative")
    private Double xCoordinate;

    @JsonProperty("yCoordinate")
    @NotNull(message = "Y coordinate is required")
    @PositiveOrZero(message = "Y coordinate cannot be negative")
    private Double yCoordinate;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;
}