package com.selection_point.controller;

import com.selection_point.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/offer")
@CrossOrigin(origins = "http://localhost:5173")
public class OfferVerifyController {

    @Autowired
    private SchoolRepository schoolRepository;

    @GetMapping("/verify/{code}")
    public Map<String, Object> verifyOffer(@PathVariable String code) {

        System.out.println("🔎 CODE RECEIVED: [" + code + "]");

        if (code == null || code.trim().isEmpty()) {
            return Map.of("valid", false);
        }

        boolean valid = schoolRepository
                .findByOfferCode(code)
                .isPresent();

        System.out.println("✅ VALID = " + valid);

        return Map.of("valid", valid);
    }
}
