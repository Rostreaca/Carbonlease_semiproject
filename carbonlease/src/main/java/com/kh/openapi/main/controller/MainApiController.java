package com.kh.openapi.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.openapi.main.model.service.MainApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value="api", produces="application/json; charset=UTF-8")
@RequiredArgsConstructor
public class MainApiController {

    private final MainApiService service;

    /**
     * 광역시/도별 에너지 사용량 %
     * @return ResponseEntity<List<Map<String, Object>>> (region, lat, lng, value)
     */
    @GetMapping("region")
    public ResponseEntity<List<Map<String, Object>>> getRegionStats() {
        return ResponseEntity.ok(service.getRegionMapData());
    }
}
