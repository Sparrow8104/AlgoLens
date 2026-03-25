package com.algolens.algo_lens.dtos.friend;

import lombok.Builder;

import java.util.List;

@Builder
public record UnsolvedByMeDTO(Integer contestId,
                              String index,
                              String name,
                              Integer rating,
                              List<String> tags,
                              List<String> solvedByFriends) {
}
