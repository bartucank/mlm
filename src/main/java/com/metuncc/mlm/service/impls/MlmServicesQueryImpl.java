package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.MlmQueryServices;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MlmServicesQueryImpl implements MlmQueryServices {

    private final UserRepository userRepository;
    private ShelfRepository shelfRepository;


    @Override
    public UserDTO getOneUserByUserName(String username) {
        return userRepository.findByUsername(username).toDTO();
    }


    @Override
    public ShelfDTO getShelfById(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Shelf shelf = shelfRepository.getById(id);
        if(Objects.isNull(shelf)){
            throw new MLMException(ExceptionCode.SHELF_NOT_FOUND);
        }
        return shelf.toDTO();
    }
    @Override
    public List<ShelfDTO> getAllShelfs(){
        return shelfRepository.findAll().stream().map(Shelf::toDTO).collect(Collectors.toList());
    }
}
