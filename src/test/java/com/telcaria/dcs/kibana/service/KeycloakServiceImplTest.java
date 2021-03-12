package com.telcaria.dcs.kibana.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class KeycloakServiceImplTest {

//    @Autowired
//    KeycloakService keycloakService;
//
//    @Test
//    void getUsersByGroup() {
//        String expectedUsers = "user1,user2";
//        String actualUsers = keycloakService.getUsersByUseCase("uc");
//        assertEquals(expectedUsers, actualUsers);
//    }
}