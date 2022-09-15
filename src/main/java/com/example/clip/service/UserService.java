package com.example.clip.service;

import com.example.clip.exception.UserNotFoundException;
import com.example.clip.response.ListAllUsersResponse;
import com.example.clip.response.UserReportResponse;

public interface UserService {
    ListAllUsersResponse listAll();
    UserReportResponse getUserReport(String userId) throws UserNotFoundException;
}
