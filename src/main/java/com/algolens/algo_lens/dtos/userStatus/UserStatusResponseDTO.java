package com.algolens.algo_lens.dtos.userStatus;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserStatusResponseDTO {
    private String status;
    private List<SubmissionDTO> result;
}
