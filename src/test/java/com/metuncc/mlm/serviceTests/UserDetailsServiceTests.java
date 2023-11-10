package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.impls.MlmQueryServicesImpl;
import com.metuncc.mlm.service.impls.UserDetailsServiceImpl;
import com.metuncc.mlm.utils.ImageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTests {
    private DOSHelper dosHelper = new DOSHelper();
    private DTOSHelper dtosHelper = new DTOSHelper();
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDetailsServiceImpl service;


    @Test
    public void loadUserByUsername(){
        when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
        assertNotNull(service.loadUserByUsername("a"));
    }
    @Test
    public void loadUserById(){
        when(userRepository.getById(any())).thenReturn(dosHelper.user1());
        assertNotNull(service.loadUserById(1L));
    }
}
