package com.algolens.algo_lens.client;

import com.algolens.algo_lens.dtos.userInfo.UserInfoResponseDto;
import com.algolens.algo_lens.dtos.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.userStatus.UserStatusResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;


@Component
@RequiredArgsConstructor
public class CodeforcesApiClient {

    private final WebClient webClient;

    public UserInfoResponseDto getUserInfo(String handle){
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user.info")
                                .queryParam("handles",handle)
                                .build()
                )
                .retrieve()
                .bodyToMono(UserInfoResponseDto.class)
                .block();
    }

    public UserStatusResponseDTO getUserSubmissions(String handle){
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user.status")
                                .queryParam("handles",handle)
                                .build()
                )
                .retrieve()
                .bodyToMono(UserStatusResponseDTO.class)
                .block();
    }

    public UserRatingResponseDTO getUserRatings(String handle){
        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user.rating")
                                .queryParam("handles",handle)
                                .build()
                )
                .retrieve()
                .bodyToMono(UserRatingResponseDTO.class)
                .block();
    }


}
