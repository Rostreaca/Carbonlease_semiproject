package com.kh.openapi.sidebar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.openapi.sidebar.model.dto.SidoPm25Response;
import com.kh.openapi.sidebar.model.dto.StationAirResponse;
import com.kh.openapi.sidebar.model.service.AirService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/air")
@RequiredArgsConstructor
public class AirController {
	
	private final AirService service;
	
	@GetMapping("/station")
	public ResponseEntity<StationAirResponse> getStation(@RequestParam String stationName) {
	    return ResponseEntity.ok(service.getStationAir(stationName));
	}

	@GetMapping("/sido")
	public ResponseEntity<SidoPm25Response> getSidoAvg(@RequestParam String sido) {
	    return ResponseEntity.ok(service.getSidoPm25Average(sido));
	}


}
