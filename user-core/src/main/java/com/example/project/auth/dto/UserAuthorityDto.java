package com.example.project.auth.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class UserAuthorityDto {
    List<String> authorities;
}
