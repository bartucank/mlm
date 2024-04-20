package com.metuncc.mlm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EbookDTO extends MLMBaseClassDTO{

    private byte[] data;

    private String name;

    private String type;
}
