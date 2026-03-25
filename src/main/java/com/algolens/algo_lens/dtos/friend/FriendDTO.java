package com.algolens.algo_lens.dtos.friend;

import lombok.Builder;

@Builder
public record FriendDTO(
        String handle,
        Integer rating,
        Integer maxRating,
        String rank,
        String avatar,
        int contestsParticipated
) {
}
