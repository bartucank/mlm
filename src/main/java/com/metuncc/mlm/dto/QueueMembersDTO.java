package com.metuncc.mlm.dto;

import com.metuncc.mlm.entity.User;
import com.metuncc.mlm.entity.enums.BorrowStatus;
import lombok.Data;

@Data
public class QueueMembersDTO {
    private UserDTO userDTO;
    private String enterDate;
    private String takeDate;
    private String returnDate;

    private String statusDesc;
    private BorrowStatus status;

}
