package com.selection_point.service;

import org.springframework.http.*;
        import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class SmsService {

    // ❌ Hardcode mat rakho (example ke liye yahan hai)
    private static final String API_KEY = "WYgowHVS6DQfcabOR5lUBrJ4hqKjZ0IkNCFeL3PTtmAMsvdzx9erpHLW92cIwEPi0GhxKUm8Z5q4QDfM";
    private static final String URL = "https://www.fast2sms.com/dev/bulkV2";

    public void sendSms(String mobile, String message) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("route", "q");
            body.put("numbers", mobile);
            body.put("message", message);

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForObject(URL, request, String.class);

            System.out.println("✅ SMS sent to " + mobile);

        } catch (Exception e) {
            // 🔥 MOST IMPORTANT LINE
            System.err.println("⚠️ SMS FAILED but ignored: " + e.getMessage());
        }
    }
}
