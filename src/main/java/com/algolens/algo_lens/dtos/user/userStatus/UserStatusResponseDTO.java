package com.algolens.algo_lens.dtos.user.userStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserStatusResponseDTO implements Serializable {
    private String status;
    private List<SubmissionDTO> result;
}
