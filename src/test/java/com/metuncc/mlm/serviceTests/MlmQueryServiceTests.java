package com.metuncc.mlm.serviceTests;

import com.metuncc.mlm.datas.DOSHelper;
import com.metuncc.mlm.datas.DTOSHelper;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.Role;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.impls.MlmQueryServicesImpl;
import com.metuncc.mlm.utils.ImageUtil;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseMaterialRepository courseMaterialRepository;
    @Mock
    private  Authentication authentication;
    @Mock
    private  SecurityContextHolder securityContextHolder;

    @Mock
    private static SecurityContext securityContext;

    @InjectMocks
    private MlmQueryServicesImpl service;

    @BeforeEach
    public void initSecurityContext() {
        authentication = mock(Authentication.class);
        lenient().when(authentication.getPrincipal()).thenReturn(JwtUserDetails.create(dosHelper.user1()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    public void deleteSecurityContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void getOneUserByUserName() {
//        when(userRepository.findByUsername(any())).thenReturn(dosHelper.user1());
//        service.getOneUserByUserName("asdf");
    }

    @Test
    public void getShelfById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getShelfById(null);
        });
    }

    @Test
    public void getShelfById_invalid_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(shelfRepository.getById(any())).thenReturn(null);
            service.getShelfById(1L);
        });
    }

    @Test
    public void getShelfById_valid_case() {
        when(shelfRepository.getById(any())).thenReturn(dosHelper.shelf1());
        service.getShelfById(1L);
    }

    @Test
    public void getAllShelfs() {
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
    public void getBookById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getBookById(null);
        });
    }

    @Test
    public void getBookbyId_invalid_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(bookRepository.getById(any())).thenReturn(null);
            service.getBookById(1L);
        });
    }

//    @DisplayName("getCourseMaterialbyId with valid id")
//    @Test
//    public void getCourseMaterialById_valid_case() {
//        when(courseMaterialRepository.getById(any())).thenReturn(dosHelper.courseMaterial1());
//        assertNotNull(service.getCourseMaterialById(1L));
//    }

    @DisplayName("getCourseMaterialById with id null")
    @Test
    public void getCourseMaterialById_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseMaterialById(null);
        });
    }

    @DisplayName("getCourseMaterialById with invalid id ")
    @Test
    public void getCourseMaterialById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseMaterialRepository.getById(any())).thenReturn(null);
            service.getCourseMaterialById(1L);
        });
    }

    @DisplayName("getCoursesForLecturer")
    @Test
    @WithMockUser(username = "username",  roles = "lec")
    public void getCoursesForLecturer() {
        when(courseRepository.getCoursesByLecturerId(any())).thenReturn(List.of(dosHelper.course1()));
        assertNull(service.getCoursesForLecturer());
    }

    @DisplayName("getCoursesForUser")
    @Test
    @WithMockUser(username = "username", password = "password", roles = "user")
    public void getCoursesForUser() {
        when(courseRepository.getAllPublicCoursesAndRegisteredCourses(any())).thenReturn(List.of(dosHelper.course1()));
        assertNotNull(service.getCoursesForUser());
    }

    @DisplayName("getCourseByIdForLecturer with valid id")
    @Test
    public void getCourseByIdForLecturer_valid_case() {
        when(courseRepository.getById(any())).thenReturn(dosHelper.course1());
        assertNotNull(service.getCourseByIdForLecturer(1L));
    }

    @DisplayName("getCourseByIdForLecturer with id null")
    @Test
    public void getCourseByIdForLecturer_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseByIdForLecturer(null);
        });
    }

    @DisplayName("getCourseByIdForLecturer with invalid id")
    @Test
    public void getCourseByIdForLecturer_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseRepository.getById(any())).thenReturn(null);
            service.getCourseByIdForLecturer(1L);
        });
    }

    @DisplayName("getCourseById with valid id")
    @Test
    public void getCourseById_valid_case() {
        when(courseRepository.getById(any())).thenReturn(dosHelper.course1());
        assertNotNull(service.getCourseById(1L));
    }

    @DisplayName("getCourseById with id null")
    @Test
    public void getCourseById_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.getCourseById(null);
        });
    }

    @DisplayName("getCourseById with invalid id")
    @Test
    public void getCourseById_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            when(courseRepository.getById(any())).thenReturn(null);
            service.getCourseById(1L);
        });
    }

    @DisplayName("getCourseStudentExcelTemplate")
    @Test
    public void getCourseStudentExcelTemplate() {
        assertNotNull(service.getCourseStudentExcelTemplate());
    }

    @Test
    public void updateRoleOfUser_valid_case() {
        when(userRepository.getById(any())).thenReturn(dosHelper.user2());
        assertNotNull(service.updateRoleOfUser(2L, Role.LIB));

    }

    @DisplayName("updateRoleOfUser with null id")
    @Test
    public void updateRoleOfUser_null_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateRoleOfUser(null, Role.LIB);
        });
    }

    @DisplayName("updateRoleOfUser with null role")
    @Test
    public void updateRoleOfUser_null_case2() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            service.updateRoleOfUser(1L, null);
        });
    }

    @DisplayName("updateRoleOfUser with invalid id")
    @Test
    public void updateRoleOfUser_invalid_case() {
        MLMException thrown = assertThrows(MLMException.class, () -> {
            lenient().when(userRepository.getById(any())).thenReturn(null);
            service.updateRoleOfUser(3L, Role.LIB);
        });
    }





}
