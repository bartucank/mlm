package com.metuncc.mlm.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ImageDTO extends MLMBaseClassDTO{

    private byte[] imageData;

    private String name;

    private String type;
}
