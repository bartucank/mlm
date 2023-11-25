package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.repository.VerificationCodeRepository;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.service.impls.MlmServicesImpl;
import com.metuncc.mlm.utils.MailUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class MlmServiceTests {
    private DOSHelper dosHelper = new DOSHelper();
    private DTOSHelper dtosHelper = new DTOSHelper();
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ShelfRepository shelfRepository;
    @Mock
    private ImageRepository imageRepository;

    @Mock
    private MailUtil mailUtil;
    @Mock
    private VerificationCodeRepository verificationCodeRepository;
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
        userRequest.setEmail("a@metu.edu.tr");
        userRequest.setNameSurname("full name");
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
            service.createUser(userRequest);
        });
        assertNotNull(thrown.getMessage());
    }

    @Test
    public void createUser_valid_case(){
//        UserRequest userRequest = new UserRequest();
//        userRequest.setUsername("username");
//        userRequest.setPass("123");
//        userRequest.setNameSurname("full name");
//        userRequest.setEmail("a@metu.edu.tr");
//        when(userRepository.findByUsername(any())).thenReturn(null);
//        when(passwordEncoder.encode(any())).thenReturn("1");
//        when(userRepository.save(any())).thenReturn(dosHelper.user1());
//        assertNotNull(service.createUser(userRequest));
    }

    @Test
    public void createShelf_nullRequest(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.createShelf(request);
        });
    }

    @Test
    public void createShelf_valid_case(){
        when( shelfRepository.save(any())).thenReturn(dosHelper.shelf1());
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        assertNotNull(service.createShelf(request));
    }

    @Test
    public void updateShelf_nullRequest(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(null);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateShelf(request);
        });
    }

    @Test
    public void updateShelf_invalidId(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(shelfRepository.getById(any())).thenReturn(null);
            service.updateShelf(request);
        });
    }

    @Test
    public void updateShelf_valid_case(){
        ShelfCreateRequest request = new ShelfCreateRequest();
        request.setFloor("1");
        request.setId(1L);
        when(shelfRepository.getById(any())).thenReturn(new Shelf());
        service.updateShelf(request);
    }

    @Test
    public void uploadImage() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );
        Image img = new Image();
        img.setId(1L);
        when(imageRepository.save(any())).thenReturn(img);
        StatusDTO result = service.uploadImage(multipartFile);

    }
}
