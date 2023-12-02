package com.liella.project.model.dto.user;

import lombok.Data;

@Data
public class UpdateUserDetailRequest {
    private String userName;
    private String userAvatar;
    private Long id;
}
