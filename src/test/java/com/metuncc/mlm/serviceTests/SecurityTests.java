package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.impls.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SecurityTests {
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
