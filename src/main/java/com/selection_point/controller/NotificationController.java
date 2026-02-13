package com.selection_point.controller;

import com.selection_point.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/student")
    public void notifyStudent(@RequestParam String mobile,
                              @RequestParam String message) {
        notificationService.notifyStudent(mobile, message);
    }

    @PostMapping("/teacher")
    public void notifyTeacher(@RequestParam String message) {
        notificationService.notifyTeacher(message);
    }
}
