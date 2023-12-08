package com.metuncc.mlm.service.impls;

import com.metuncc.mlm.api.request.FindBookRequest;
import com.metuncc.mlm.api.response.BookDTOListResponse;
import com.metuncc.mlm.api.request.FindUserRequest;
import com.metuncc.mlm.api.response.RoomDTOListResponse;
import com.metuncc.mlm.api.response.ShelfDTOListResponse;
import com.metuncc.mlm.dto.*;
import com.metuncc.mlm.api.response.UserDTOListResponse;
import com.metuncc.mlm.entity.*;
import com.metuncc.mlm.exception.ExceptionCode;
import com.metuncc.mlm.exception.MLMException;
import com.metuncc.mlm.repository.*;
import com.metuncc.mlm.repository.ImageRepository;
import com.metuncc.mlm.repository.RoomRepository;
import com.metuncc.mlm.repository.ShelfRepository;
import com.metuncc.mlm.repository.UserRepository;
import com.metuncc.mlm.repository.specifications.BookSpecification;
import com.metuncc.mlm.repository.specifications.UserSpecification;
import com.metuncc.mlm.security.JwtUserDetails;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.utils.ImageUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private BookRepository bookRepository;
    private CopyCardRepository copyCardRepository;
    private RoomSlotRepository roomSlotRepository;

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
    public BookDTO getBookById(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Book book = bookRepository.getById(id);
        if(Objects.isNull(book)){
            throw new MLMException(ExceptionCode.BOOK_NOT_FOUND);
        }
        return book.toDTO();
    }

    @Override
    public BookDTOListResponse getBooksByShelfId(Long shelfId) {
        BookDTOListResponse response = new BookDTOListResponse();
        response.setBookDTOList(bookRepository.getBooksByShelfId(shelfId).stream().map(Book::toDTO).collect(Collectors.toList()));
        return response;
    }


    @Override
    public UserDTOListResponse getUsersBySpecifications(FindUserRequest request){
        if(Objects.isNull(request)){
            request = new FindUserRequest();
            request.setSize(7);
            request.setPage(0);
        }
        UserSpecification userSpecification = new UserSpecification(
                request.getRole(),
                request.getFullName(),
                request.getUsername(),
                request.getVerified(),
                request.getEmail()
        );
        if(Objects.isNull(request.getSize()) ||Objects.isNull(request.getPage())){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<User> usersPage = userRepository.findAll(userSpecification, pageable);
        List<UserDTO> dtos = usersPage.getContent().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
        UserDTOListResponse response = new UserDTOListResponse();
        response.setUserDTOList(dtos);
        response.setTotalPage(usersPage.getTotalPages());
        response.setTotalResult(usersPage.getTotalElements());
        response.setSize(request.getSize());
        response.setPage(request.getPage());
        return response;
    }

    @Override
    public UserDTO getUserDetails() {
        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();
            User user = userRepository.getById(jwtUser.getId());
            if(Objects.nonNull(user)){
                return user.toDTO();
            }
        }catch (Exception e){
            throw new MLMException(ExceptionCode.SESSION_EXPERIED_PLEASE_LOGIN);
        }
        throw new MLMException(ExceptionCode.SESSION_EXPERIED_PLEASE_LOGIN);
    }

    @Override
    public BookDTOListResponse getBooksBySpecification(FindBookRequest request) {
        if(Objects.isNull(request)){
            request = new FindBookRequest();
            request.setPage(0);
            request.setSize(7);
        }
        BookSpecification  bookSpecification = new BookSpecification(
                request.getName(),
                request.getAuthor(),
                request.getPublisher(),
                request.getDescription(),
                request.getIsbn(),
                request.getPublicationDate(),
                request.getBarcode(),
                request.getCategory(),
                request.getStatus()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Book> bookPage = bookRepository.findAll(bookSpecification, pageable);
        List<BookDTO> bookDTOS = bookPage.getContent().stream()
                .map(Book::toDTO)
                .collect(Collectors.toList());
        BookDTOListResponse response = new BookDTOListResponse();
        response.setBookDTOList(bookDTOS);
        response.setTotalPage(bookPage.getTotalPages());
        response.setTotalResult(bookPage.getTotalElements());
        response.setSize(request.getSize());
        response.setPage(request.getPage());
        return response;

    }

    @Override
    public RoomDTO getRoomById(Long id){
        if(Objects.isNull(id)){
            throw new MLMException(ExceptionCode.INVALID_REQUEST);
        }
        Room room = roomRepository.getById(id);
        if(Objects.isNull(room)){
            throw new MLMException(ExceptionCode.ROOM_NOT_FOUND);
        }

        return room.toDTO();
    }

    @Override
    public RoomDTOListResponse getRooms(){
        return new RoomDTOListResponse(roomRepository.findAll().stream().map(Room::toDTO).collect(Collectors.toList()));
    }

    @Override
    public CopyCardDTO getCopyCardDetails(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        JwtUserDetails jwtUser = (JwtUserDetails) auth.getPrincipal();

        return copyCardRepository.getByUser(jwtUser.getId()).toDTO();
    }
}
