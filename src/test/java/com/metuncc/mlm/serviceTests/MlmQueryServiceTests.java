package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.BookRepository;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.impls.MlmQueryServicesImpl;
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
public class MlmQueryServiceTests {
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
    private BookRepository bookRepository;

    @InjectMocks
    private MlmQueryServicesImpl service;


    @Test
    public void getOneUserByUserName(){
        when(userRepository.findByUsername(any())).thenReturn(new User());
        service.getOneUserByUserName("asdf");
    }

    @Test
    public void getShelfById_invalid_case(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getShelfById(null);
        });
    }

    @Test
    public void getShelfById_invalid_case2(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(shelfRepository.getById(any())).thenReturn(null);
            service.getShelfById(1L);
        });
    }

    @Test
    public void getShelfById_valid_case(){
        when(shelfRepository.getById(any())).thenReturn(dosHelper.shelf1());
        service.getShelfById(1L);
    }

    @Test
    public void getAllShelfs(){
        when(shelfRepository.findAll()).thenReturn(List.of(dosHelper.shelf1()));
        service.getAllShelfs();
    }

    @Test
    public void getImageById() throws IOException {
        ClassPathResource resource = new ClassPathResource("image.jpg");
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",
                "image.jpg",
                "image/jpeg",
                resource.getInputStream()
        );

        Image image = new Image();
        image.setImageData(ImageUtil.compressImage(multipartFile.getBytes()));
        image.setName(multipartFile.getOriginalFilename());
        image.setType(multipartFile.getContentType());


        when(imageRepository.getImageById(any())).thenReturn(image);
        service.getImageById(1L);
    }
    @Test
    public void getImageById_null_case() throws IOException {


        when(imageRepository.getImageById(any())).thenReturn(null);
        service.getImageById(1L);
    }
    @Test
    public void getBookById_valid_case() {
        when(bookRepository.getById(any())).thenReturn(dosHelper.book1());
        service.getBookById(1L);
    }
    @Test
    public void getBookById_invalid_case(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getBookById(null);
        });
    }
    @Test
    public void getBookbyId_invalid_case2(){
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(bookRepository.getById(any())).thenReturn(null);
            service.getBookById(1L);
        });
    }
}
