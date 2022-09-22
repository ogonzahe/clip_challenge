package com.example.clip.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.example.clip.response.ListAllUsersResponse;
import com.example.clip.response.UserReportResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-integration.properties")
@Sql(scripts = "classpath:sql/users/list-all-users-fixture.sql")
class UserControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    private static final String ENDPOINT = "http://localhost:%s/api/clip/user/%s";

    @Test
    void shouldGetAllUsersWithPaymentsSuccessfully() {
        ResponseEntity<ListAllUsersResponse> listAllUsersResponse = restTemplate.getForEntity(String.format(ENDPOINT, port, "listAll"), ListAllUsersResponse.class);
        assertEquals(HttpStatus.OK, listAllUsersResponse.getStatusCode());
        assertNotNull(listAllUsersResponse.getBody());
        List<String> actualUserIds = listAllUsersResponse.getBody().getUsersList();
        assertEquals(3, actualUserIds.size());
        List<String> expectedUserIds = List.of("user-id-1", "user-id-2", "user-id-3");
        assertTrue(actualUserIds.containsAll(expectedUserIds));
    }

    @ParameterizedTest
    @ValueSource(strings = {"user-id-1", "user-id-2", "user-id-3"})
    void shouldGenerateUserReportSuccessfully(String userId) {
        ResponseEntity<UserReportResponse> userReportResponse = restTemplate.getForEntity(String.format(ENDPOINT, port, userId + "/report"), UserReportResponse.class);
        assertEquals(HttpStatus.OK, userReportResponse.getStatusCode());
        assertNotNull(userReportResponse.getBody());
        UserReportResponse expectedResponse = getExpectedUserReportResponse(userId);
        UserReportResponse actualResponse = userReportResponse.getBody();
        assertEquals(expectedResponse.getUserName(), actualResponse.getUserName());
        assertEquals(expectedResponse.getNewPayments(), actualResponse.getNewPayments());
        assertEquals(expectedResponse.getNewPaymentsAmount(), actualResponse.getNewPaymentsAmount());
        assertEquals(expectedResponse.getPaymentsSum(), actualResponse.getPaymentsSum());
    }

    private UserReportResponse getExpectedUserReportResponse(String userId) {
        if("user-id-1".equals(userId)) {
            return buildResponse(userId, new BigDecimal(2L), 1, new BigDecimal(1L));
        } else if("user-id-2".equals(userId)) {
            return buildResponse(userId, new BigDecimal(1L), 0, new BigDecimal(0L));
        } else if("user-id-3".equals(userId)) {
            return buildResponse(userId, new BigDecimal(1L), 0, new BigDecimal(0L));
        }
        throw new IllegalArgumentException("Invalid user id");
    }

    private UserReportResponse buildResponse(String userName, 
                                             BigDecimal paymentsSum, 
                                             long newPayments, 
                                             BigDecimal newPaymentsAmount) {
        return UserReportResponse.builder()
                                 .userName(userName)
                                 .paymentsSum(paymentsSum.setScale(2, RoundingMode.DOWN))
                                 .newPayments(newPayments)
                                 .newPaymentsAmount(newPaymentsAmount.setScale(2, RoundingMode.DOWN))
                                 .build();
    }
}
