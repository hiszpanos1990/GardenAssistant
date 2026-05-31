package com.example.gardenassistant.exception;

public class GardenNotFoundException extends RuntimeException {
    public GardenNotFoundException(String message) {
        super(message);
    }
}
