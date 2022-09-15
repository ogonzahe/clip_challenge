package com.example.clip.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ListAllUsersResponse {
    private List<String> usersList;
}
