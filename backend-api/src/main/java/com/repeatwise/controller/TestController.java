package com.repeatwise.controller;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/ping")
    public ResponseEntity<PingResponse> ping() {
        final var response = new PingResponse("ok", Instant.now());
        return ResponseEntity.ok(response);
    }

    public record PingResponse(String status, Instant timestamp) {
    }
}

