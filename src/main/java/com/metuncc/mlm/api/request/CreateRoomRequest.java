package com.metuncc.mlm.api.request;

import com.metuncc.mlm.entity.Image;
import com.metuncc.mlm.entity.enums.BookCategory;
import com.metuncc.mlm.entity.enums.BookStatus;
import lombok.Data;

import javax.persistence.OneToOne;
import java.time.LocalDate;

@Data
public class CreateRoomRequest {
    private String name;
    private Long imageId;
}
