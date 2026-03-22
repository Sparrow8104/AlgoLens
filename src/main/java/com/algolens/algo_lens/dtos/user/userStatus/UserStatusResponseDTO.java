package com.algolens.algo_lens.dtos.user.userStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserStatusResponseDTO implements Serializable {
    private String status;
    private List<SubmissionDTO> result;
}
