package com.selection_point.config;

import java.util.UUID;

public class SchoolCodeUtil {

    public static String generate(String schoolName) {
        String prefix = schoolName
                .replaceAll("\\s+", "")
                .substring(0, Math.min(4, schoolName.length()))
                .toUpperCase();

        return prefix + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }
}
