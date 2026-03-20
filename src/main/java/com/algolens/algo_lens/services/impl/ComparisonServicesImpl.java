package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareRequestDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareResponseDTO;
import com.algolens.algo_lens.dtos.comparison.UserSubmissionResultDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.mapper.ComparisonMapper;
import com.algolens.algo_lens.services.service.ComparisonServices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComparisonServicesImpl implements ComparisonServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final ComparisonMapper comparisonMapper;

    public ComparisonServicesImpl(CodeforcesApiClient codeforcesApiClient, ComparisonMapper comparisonMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.comparisonMapper = comparisonMapper;
    }

    @Override
    public RatingComparisonDTO compareRatings(String handle1, String handle2) {
        CodeforcesUserDTO user1=codeforcesApiClient.getUserInfo(handle1).getResult().getFirst();
        CodeforcesUserDTO user2=codeforcesApiClient.getUserInfo(handle2).getResult().getFirst();
        int contestsParticipated1=codeforcesApiClient.getUserRatings(handle1).getResult().size();
        int contestsParticipated2=codeforcesApiClient.getUserRatings(handle2).getResult().size();
        return comparisonMapper.mapToRatingComparisonDTO(
                user1, user2,
                contestsParticipated1,
                contestsParticipated2
        );
    }

    @Override
    public SubmissionCompareResponseDTO findSubmissions(SubmissionCompareRequestDTO request) {
        System.out.println("Fetching submission for handle1 "+ request.handle1());
        List<SubmissionDTO> submissions1=codeforcesApiClient.getUserSubmissions(request.handle1()).getResult();

        System.out.println(submissions1.size()+"Got submission for handle1"+ request.handle1());
        System.out.println("Fetching submission for handle2 "+ request.handle2());
        List<SubmissionDTO> submissions2=codeforcesApiClient.getUserSubmissions(request.handle2()).getResult();

        System.out.println("Got submission for handle2"+submissions2.size()+"and" +request.handle2());
        SubmissionDTO best1=bestSubmission(submissions1,request.contestId(),request.index());
        SubmissionDTO best2=bestSubmission(submissions2,request.contestId(),request.index());

        UserSubmissionResultDTO user1=comparisonMapper.mapToUserSubmissionResultDTO(request.handle1(),best1);
        UserSubmissionResultDTO user2=comparisonMapper.mapToUserSubmissionResultDTO(request.handle2(),best2);
        return comparisonMapper.mapToSubmissionCompareResponseDTO(
                request.contestId(),
                request.index(),
                user1,
                user2
        );
    }

    public SubmissionDTO bestSubmission(List<SubmissionDTO> submissions,int contestId,String index) {
        submissions.stream().limit(3).forEach(s -> {
            if (s.getProblem() != null) {
                System.out.println("contestId=" + s.getProblem().getContestId()
                        + " index=" + s.getProblem().getIndex());
            }
        });
        System.out.println("Looking for: contestId=" + contestId + " index=" + index);
        List<SubmissionDTO> matching=submissions.stream()
                .filter(s-> s.getProblem()!=null&&
                        s.getProblem().getContestId()!=null&&
                                s.getProblem().getContestId().equals(contestId)&&
                        index.equalsIgnoreCase(s.getProblem().getIndex())
                        ).toList();

        System.out.println(matching.size()+"matching");
        if(matching.isEmpty())
            return null;

        return matching.stream()
                .filter(s->"OK".equals(s.getVerdict()))
                .findFirst()
                .orElse(matching.getFirst());


    }
}
