package com.selection_point.service;

import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    public void notifyStudent(String mobile, String message) {
        System.out.println("SMS to Student [" + mobile + "]: " + message);
    }

    public void notifyTeacher(String message) {
        System.out.println("SMS to Teacher: " + message);
    }
}
