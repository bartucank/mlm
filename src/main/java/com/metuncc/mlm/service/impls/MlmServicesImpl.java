package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.Shelf;
import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.RoomRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.service.MlmServices;
import com.metuncc.mlm.utils.ImageUtil;
import lombok.AllArgsConstructor;
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
}
