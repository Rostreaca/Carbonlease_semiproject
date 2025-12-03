package com.kh.openapi.common.util;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OpenApiResponseUtil {

    //  itemObj가 List, Map 등 다양한 타입일 때 안전하게 List<Map<String, Object>>로 변환해주는 "헬퍼 메서드"
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> extractItemList(Object itemObj) {
        if (itemObj instanceof List) {
            List<?> rawList = (List<?>) itemObj;
            if (!rawList.isEmpty() && rawList.get(0) instanceof List) {
                List<Map<String, Object>> items = new java.util.ArrayList<>();
                for (Object sub : rawList) {
                    if (sub instanceof List) {
                        for (Object m : (List<?>) sub) {
                            if (m instanceof Map) items.add((Map<String, Object>) m);
                        }
                    }
                }
                return items;
            } else {
                return (List<Map<String, Object>>) itemObj;
            }
        } else if (itemObj instanceof Map) {
            return List.of((Map<String, Object>) itemObj);
        } else {
            return List.of();
        }
    }

    //  JSON 전체에서 body, items, item까지 한 번에 파싱해서 최종적으로 List<Map<String, Object>> 형태로 반환하는 "공통 파싱 메서드"
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseApiItems(ObjectMapper om, String json) {
        try {
            Object parsed = om.readValue(json, Object.class);
            Map<String, Object> body;
            if (parsed instanceof Map) {
                body = (Map<String, Object>) ((Map<String, Object>) parsed).get("body");
            } else {
                return List.of();
            }
            Object itemsObj = body.get("items");
            Object itemObj;
            if (itemsObj instanceof Map) {
                itemObj = ((Map<String, Object>) itemsObj).get("item");
            } else if (itemsObj instanceof List) {
                itemObj = itemsObj;
            } else {
                itemObj = null;
            }
            return extractItemList(itemObj);
        } catch (Exception e) {
            return List.of();
        }
    }
}
