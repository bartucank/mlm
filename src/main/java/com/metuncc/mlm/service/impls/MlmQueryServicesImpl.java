package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.api.response.UserDTOListResponse;
import com.metuncc.mlm.dto.ImageDTO;
import com.metuncc.mlm.dto.ShelfDTO;
import com.metuncc.mlm.dto.UserDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.RoomRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.repository.specifications.UserSpecification;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.utils.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MlmQueryServicesImpl implements MlmQueryServices {

    private final UserRepository userRepository;
    private ShelfRepository shelfRepository;
    private RoomRepository roomRepository;
    private ImageRepository imageRepository;

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
    public ShelfDTOListResponse getAllShelfs(){
        ShelfDTOListResponse response = new ShelfDTOListResponse();
        response.setShelfDTOList(shelfRepository.findAll().stream().map(Shelf::toDTO).collect(Collectors.toList()));
        return response;
    }

    @Override
    public ImageDTO getImageById(Long id) {
        Image dbImage = imageRepository.getImageById(id);
        if(Objects.nonNull(dbImage)){
            ImageDTO dto =  ImageDTO
                    .builder()
                    .name(dbImage.getName())
                    .imageData(ImageUtil.decompressImage(dbImage.getImageData()))
                    .build();
            dto.setId(id);
            return dto;
        }else{
            return null;
        }
    }

    @Override
    public UserDTOListResponse getUsersBySpecifications(FindUserRequest request){
        if(Objects.isNull(request)){
            request = new FindUserRequest();
        }
        UserSpecification userSpecification = new UserSpecification(
                request.getRole(),
                request.getFullName(),
                request.getUsername(),
                request.getVerified(),
                request.getEmail()
        );
        List<User> users = userRepository.findAll(userSpecification);
        List<UserDTO> dtos = users.stream().map(User::toDTO).collect(Collectors.toList());
        UserDTOListResponse response = new UserDTOListResponse();
        response.setUserDTOList(dtos);
        return response;
    }
}
