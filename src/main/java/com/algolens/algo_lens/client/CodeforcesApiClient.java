package com.algolens.algo_lens.client;

import com.algolens.algo_lens.dtos.contest.CodeforcesContestResponseDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserInfoResponseDto;
import com.algolens.algo_lens.dtos.user.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.user.userStatus.UserStatusResponseDTO;
import com.algolens.algo_lens.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@RequiredArgsConstructor
public class CodeforcesApiClient {

    private final WebClient webClient;

    public UserInfoResponseDto getUserInfo(String handle){
        try{
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
        }catch (WebClientResponseException e){
            throw new ExternalApiException("Failed to fetch user info from codeforces API");
        }

    }

    public UserStatusResponseDTO getUserSubmissions(String handle) {
        try {
            return webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/user.status")
                                    .queryParam("handle",handle)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(UserStatusResponseDTO.class)
                    .block();

        }catch (WebClientResponseException e) {
            throw new ExternalApiException("Failed to fetch submissions from codeforces API");
        }


    }

    public UserRatingResponseDTO getUserRatings(String handle){
        try{
            return webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/user.rating")
                                    .queryParam("handle",handle)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(UserRatingResponseDTO.class)
                    .block();
        }catch (WebClientResponseException e){
            throw new ExternalApiException("Failed to fetch ratings from codeforces API");
        }

    }

    public CodeforcesContestResponseDTO getContests(){
        try{
            return webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/contest.list")
                                    .queryParam("gym",false)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(CodeforcesContestResponseDTO.class)
                    .block();
        }catch (WebClientResponseException e){
            throw new ExternalApiException("Failed to fetch contests from codeforces API");
        }
    }


}
