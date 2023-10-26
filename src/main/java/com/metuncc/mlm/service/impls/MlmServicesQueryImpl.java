package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MlmServicesQueryImpl implements MlmQueryServices {

    private final UserRepository userRepository;


    @Override
    public UserDTO getOneUserByUserName(String username) {
        return userRepository.findByUsername(username).toDTO();
    }
}
