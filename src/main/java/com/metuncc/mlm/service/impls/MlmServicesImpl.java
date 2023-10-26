package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.MlmServices;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class MlmServicesImpl implements MlmServices {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    private final StatusDTO success = StatusDTO.builder().statusCode("S").msg("Success!").build();
    @Override
    public StatusDTO createUser(UserRequest userRequest) {
        if(Objects.isNull(userRequest) || Objects.isNull(userRequest.getUsername()) || Objects.isNull(userRequest.getPass()) ||Objects.isNull(userRequest.getNameSurname())){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        if(Objects.nonNull(userRepository.findByUsername(userRequest.getUsername()))){
            throw new MLMException(ExceptionCode.USERNAME_ALREADY_TAKEN);
        }
        User user = new User().fromRequest(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPass()));
        userRepository.save(user);
        return success;
    }
}
