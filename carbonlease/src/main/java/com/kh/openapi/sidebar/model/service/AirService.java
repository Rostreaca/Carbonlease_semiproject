package com.kh.openapi.sidebar.model.service;

import com.kh.openapi.sidebar.model.dto.SidoPm25Response;
import com.kh.openapi.sidebar.model.dto.StationAirResponse;

public interface AirService {

    StationAirResponse getStationAir(String name);

    SidoPm25Response getSidoPm25(String name);

}
