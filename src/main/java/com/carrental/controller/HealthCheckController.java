package com.carrental.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health check",
        description = "Endpoint for checking the health of the application")
@RestController
@RequestMapping(value = "/health")
public class HealthCheckController {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Health check",
            description = "Check if the application is running")
    public void healthCheck() {
    }
}
