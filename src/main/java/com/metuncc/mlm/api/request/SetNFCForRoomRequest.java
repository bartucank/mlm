package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class SetNFCForRoomRequest {
    private Long roomId;
    private String nfcNo;
}
