package com.algolens.algo_lens.dtos.user.userInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfoResponseDto implements Serializable {
    private String status;
    List<CodeforcesUserDTO> result;

}
