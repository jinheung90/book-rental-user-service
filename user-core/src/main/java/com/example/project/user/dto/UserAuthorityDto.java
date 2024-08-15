package com.example.project.user.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
@Schema(description = "유저 엑세스 권한 정보")
public class UserAuthorityDto {
    @Schema(description = "권한 리스트")
    List<String> authorities;
}
