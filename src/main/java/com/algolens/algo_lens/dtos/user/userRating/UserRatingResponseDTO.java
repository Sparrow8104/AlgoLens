package com.algolens.algo_lens.dtos.user.userRating;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class UserRatingResponseDTO implements Serializable {
    private String status;
    private List<RatingChangeDTO> result;
}
