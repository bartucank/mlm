package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.service.impls.MlmServicesImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MlmServiceTests {
    private DOSHelper dosHelper = new DOSHelper();
    private DTOSHelper dtosHelper = new DTOSHelper();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MlmServicesImpl service;

    @Test
    public void createUser_emptyRequest(){
        UserRequest userRequest = null;
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createUser(userRequest);

        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_already_exists_case(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setNameSurname("full name");
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
            service.createUser(userRequest);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_valid_case(){
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername("username");
        userRequest.setPass("123");
        userRequest.setNameSurname("full name");
        when(userRepository.findByUsername(any())).thenReturn(null);
        when(passwordEncoder.encode(any())).thenReturn("1");
        assertNotNull(service.createUser(userRequest));
    }
}
