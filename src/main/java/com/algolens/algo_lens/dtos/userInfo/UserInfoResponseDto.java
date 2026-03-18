package com.algolens.algo_lens.dtos.userInfo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserInfoResponseDto {
    private String status;
    List<CodeforcesUserDTO> result;

}
