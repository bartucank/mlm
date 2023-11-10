package com.metuncc.mlm.service.impls;
import java.util.Random;
import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.response.LoginResponse;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.VerificationCode;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.security.JwtTokenProvider;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.utils.ImageUtil;
import com.metuncc.mlm.utils.MailUtil;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Objects;

@Service
@AllArgsConstructor
@Transactional
public class MlmServicesImpl implements MlmServices {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private ShelfRepository shelfRepository;
    private RoomRepository roomRepository;
    private ImageRepository imageRepository;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    private MailUtil mailUtil;
    private VerificationCodeRepository verificationCodeRepository;
    private final StatusDTO success = StatusDTO.builder().statusCode("S").msg("Success!").build();
    private final StatusDTO error = StatusDTO.builder().statusCode("E").msg("Error!").build();
    @Override
    public LoginResponse createUser(UserRequest userRequest) {
        if(Objects.isNull(userRequest) ||
                Objects.isNull(userRequest.getUsername()) ||
                Objects.isNull(userRequest.getPass()) ||
                Objects.isNull(userRequest.getNameSurname()) ||
                Objects.isNull(userRequest.getEmail())
        ){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        //mailUtil.sendMail();
        if(!userRequest.getEmail().endsWith("@metu.edu.tr")){
            throw new MLMException(ExceptionCode.ONLY_METU);
        }
        if (Objects.nonNull(userRepository.findByUsername(userRequest.getUsername())) ||
                Objects.nonNull(userRepository.findByEmail(userRequest.getEmail()))) {
            throw new MLMException(ExceptionCode.USERNAME_ALREADY_TAKEN);
        }
        User user = new User().fromRequest(userRequest);
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(userRequest.getPass()));
        user = userRepository.save(user);
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setUser(user);
        verificationCode.setCode(generateRandomCode(4));
        verificationCodeRepository.save(verificationCode);
        mailUtil.sendVerifyEmailEmail(user.getEmail(),verificationCode.getCode());
        return login(userRequest);
    }

    private static String generateRandomCode(int length) {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomDigit = random.nextInt(10);
            code.append(randomDigit);
        }
        return code.toString();
    }
    @Override
    public LoginResponse login(UserRequest userRequest){
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(),
                        userRequest.getPass());
        Authentication authentication = authenticationManager
                .authenticate(usernamePasswordAuthenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token =  jwtTokenProvider.generateJwtToken(authentication);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setJwt(token);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        loginResponse.setNeedVerify(!user.getVerified());
        return loginResponse;
    }

    @Override
    public StatusDTO createShelf(ShelfCreateRequest request){
        if(Objects.isNull(request) || Objects.isNull(request.getFloor())){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = new Shelf().fromRequest(request);
        shelfRepository.save(shelf);
        return success;
    }

    @Override
    public StatusDTO updateShelf(ShelfCreateRequest request){
        if(Objects.isNull(request) || Objects.isNull(request.getId()) ||Objects.isNull(request.getFloor())){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = shelfRepository.getById(request.getId());
        if(Objects.isNull(shelf)){
            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
        }
        shelf = shelf.fromRequest(request);
        shelfRepository.save(shelf);
        return success;
    }

    @Override
    public StatusDTO uploadImage(MultipartFile file) throws IOException {

        Image img= new Image();
        img.setImageData(ImageUtil.compressImage(file.getBytes()));
        img.setName(file.getOriginalFilename());
        img.setType(file.getContentType());

        img = imageRepository.save(img);
        StatusDTO.builder().statusCode("S").msg(img.getId().toString()).build();
        return success;
    }

    @Override
    public StatusDTO verifyEmail(String code){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
        User user = userRepository.getById(jwtUser.getId());
        VerificationCode verificationCode = verificationCodeRepository.getByUser(user);
        if(verificationCode.getCode().equals(code)){
            verificationCode.setIsCompleted(true);
            verificationCodeRepository.save(verificationCode);
            user.setVerified(true);
            userRepository.save(user);
            return success;
        }
        return error;
    }
}
