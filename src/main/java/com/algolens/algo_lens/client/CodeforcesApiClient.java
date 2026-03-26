package com.algolens.algo_lens.client;

import com.algolens.algo_lens.dtos.code.raw.ContestStatusResponseDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestResponseDTO;
import com.algolens.algo_lens.dtos.insight.ProblemsetResponseDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserInfoResponseDto;
import com.algolens.algo_lens.dtos.user.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.user.userStatus.UserStatusResponseDTO;
import com.algolens.algo_lens.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@RequiredArgsConstructor
public class CodeforcesApiClient {

    private final WebClient webClient;

    @Cacheable(value="userInfo",key = "#handle")
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

    @Cacheable(value="userSubmissions",key = "#handle")
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

    @Cacheable(value="userRatings",key = "#handle")
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

    @Cacheable(value="contests")
    public CodeforcesContestResponseDTO getContests(){
        try{
            return webClient.get()
                    .uri(uriBuilder ->
                            uriBuilder
                                    .path("/contest.list")
                                    .queryParam("gym",false)
                                    .queryParam("count", 10000)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(CodeforcesContestResponseDTO.class)
                    .block();
        }catch (WebClientResponseException e){
            throw new ExternalApiException("Failed to fetch contests from codeforces API");
        }
    }

    @Cacheable(value = "problemset", key = "#tag")
    public ProblemsetResponseDTO getProblemsByTag(String tag) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/problemset.problems")
                            .queryParam("tags", tag)
                            .build())
                    .retrieve()
                    .bodyToMono(ProblemsetResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalApiException(
                    "Failed to fetch problemset from Codeforces API");
        }
    }

    @Cacheable(value = "contestStatus", key = "#contestId")
    public ContestStatusResponseDTO getContestStatus(int contestId) {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/contest.status")
                            .queryParam("contestId", contestId)
                            .queryParam("count", 10000)
                            .build())
                    .retrieve()
                    .bodyToMono(ContestStatusResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new ExternalApiException(
                    "Failed to fetch contest status from Codeforces API");
        }
    }


}
