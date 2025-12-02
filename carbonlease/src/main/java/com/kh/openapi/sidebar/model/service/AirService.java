package com.kh.openapi.sidebar.model.service;

import java.util.Map;

import com.kh.openapi.sidebar.model.dto.SidoPm25Response;
import com.kh.openapi.sidebar.model.dto.StationAirResponse;

public interface AirService {
	StationAirResponse getStationAir(String stationName);
	SidoPm25Response getSidoPm25Average(String sido);

}
