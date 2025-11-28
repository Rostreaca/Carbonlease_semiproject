package com.kh.openapi.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.openapi.main.model.service.MainApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value="api", produces="application/json; charset=UTF-8")
public class MainApiController {
	
	private final MainApiService mainApiService;
	
	@GetMapping("/energy/3months")
    public List<Map<String, Object>> getStats() {
        return mainApiService.getLast3MonthsStats();
    }
	
}
