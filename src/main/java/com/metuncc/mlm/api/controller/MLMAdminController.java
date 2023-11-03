package com.metuncc.mlm.api.controller;

import com.metuncc.mlm.api.request.ShelfCreateRequest;
import com.metuncc.mlm.api.request.UserRequest;
import com.metuncc.mlm.api.service.ApiResponse;
import com.metuncc.mlm.api.service.ResponseService;
import com.metuncc.mlm.dto.StatusDTO;
import com.metuncc.mlm.service.MlmQueryServices;
import com.metuncc.mlm.service.MlmServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value ="/api/admin", produces = "application/json;charset=UTF-8")
public class MLMAdminController {
    private MlmServices mlmServices;
    private MlmQueryServices mlmQueryServices;
    private ResponseService responseService;
    public MLMAdminController(MlmServices mlmServices, MlmQueryServices mlmQueryServices, ResponseService responseService) {
        this.mlmServices = mlmServices;
        this.mlmQueryServices = mlmQueryServices;
        this.responseService = responseService;
    }

    @PostMapping("/shelf/create")
    public ResponseEntity<ApiResponse<StatusDTO>> createShelf(@RequestBody ShelfCreateRequest shelfCreateRequest){
        return responseService.createResponse(mlmServices.createShelf(shelfCreateRequest));
    }

    @PutMapping("/shelf/update")
    public ResponseEntity<ApiResponse<StatusDTO>> updateShelf(@RequestBody ShelfCreateRequest shelfCreateRequest){
        return responseService.createResponse(mlmServices.updateShelf(shelfCreateRequest));
    }


}
