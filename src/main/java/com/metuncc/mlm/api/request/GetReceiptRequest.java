package com.metuncc.mlm.api.request;

import lombok.Data;

@Data
public class GetReceiptRequest extends PageableRequest{
    private Boolean isApproved;
}
