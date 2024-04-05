package com.metuncc.mlm.integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.VerificationCode;
import com.metuncc.mlm.entity.enums.Department;
import com.metuncc.mlm.entity.enums.VerificationType;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.repository.VerificationCodeRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")

public class IntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;
    @Autowired
    private  UserRepository userRepository;


    @DisplayName("Create User Test")
    @Test
    public void testCreateUser() throws Exception {

        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("bartu");
        userRequest.setNameSurname("Bartu Can Palamut");
        userRequest.setPass("VerySecurePassword");
        userRequest.setEmail("e238622@metu.edu.tr");
        userRequest.setStudentNumber("2386225");
        userRequest.setDepartment(Department.CNG);
        userRequest.setNameSurname("a a");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(userRequest);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andReturn();

        assertEquals(200, result.getResponse().getStatus());

        String content = result.getResponse().getContentAsString();

        assertTrue(content.contains("\"needVerify\":true"));

        assertFalse(content.contains("\"jwt\":null"));
    }




}
