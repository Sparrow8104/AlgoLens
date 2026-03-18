package com.algolens.algo_lens.dtos.user.userRating;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserRatingResponseDTO {
    private String status;
    private List<RatingChangeDTO> result;
}
