package com.example.clip.controller;

import java.util.UUID;

import javax.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.clip.exception.UserNotFoundException;
import com.example.clip.response.ListAllUsersResponse;
import com.example.clip.response.UserReportResponse;
import com.example.clip.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/clip/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/listAll")
    public ResponseEntity<ListAllUsersResponse> listAllUsersWithPayment() {
        String tracingId = UUID.randomUUID().toString();
        log.debug("{} - Request to list all users received...", tracingId);
        ListAllUsersResponse listAllUsersResponse = userService.listAll();
        log.debug("{} - Executed list all users successfully...", tracingId);
        return ResponseEntity.ok(listAllUsersResponse);
    }

    @GetMapping("/{userId}/report")
    public ResponseEntity<Object> getUserReport(@PathVariable @NotBlank String userId) {
        String tracingId = UUID.randomUUID().toString();
        log.debug("{} - Request to generate user report for user {} received...", tracingId, userId);
        try {
            UserReportResponse userReportResponse = userService.getUserReport(userId);
            log.debug("{} - Executed user report successfully...", tracingId);
            return ResponseEntity.ok(userReportResponse);
        } catch (UserNotFoundException e) {
            log.debug("{} - User {} was not found...", tracingId, userId);
            return ResponseEntity.ok(e.getMessage());
        }

    }
}
