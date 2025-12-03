package com.kh.openapi.sidebar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.openapi.sidebar.model.service.AirService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/air")
@RequiredArgsConstructor
public class AirController {

    private final AirService service;

    @GetMapping("/station")
    public ResponseEntity<?> station(@RequestParam("name") String name) {
        return ResponseEntity.ok(service.getStationAir(name));
    }

    @GetMapping("/sido")
    public ResponseEntity<?> sido(@RequestParam("name") String name) {
        return ResponseEntity.ok(service.getSidoPm25(name));
    }
}

