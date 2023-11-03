package com.metuncc.mlm.service;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.entity.User;

public interface MlmServices {
    StatusDTO createUser(UserRequest userRequest);

    StatusDTO createShelf(ShelfCreateRequest request);

    StatusDTO updateShelf(ShelfCreateRequest request);
}
