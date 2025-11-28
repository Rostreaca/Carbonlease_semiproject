package com.kh.openapi.main.model.service;

import java.util.List;
import java.util.Map;

public interface MainApiService {
	List<Map<String, Object>> getLast3MonthsStats();
}
