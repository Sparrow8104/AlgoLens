package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.mapper.InsightMapper;
import com.algolens.algo_lens.services.service.InsightServices;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InsightServicesImpl implements InsightServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final InsightMapper insightMapper;

    public InsightServicesImpl(CodeforcesApiClient codeforcesApiClient, InsightMapper insightMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.insightMapper = insightMapper;
    }

    @Override
    public List<WeakTopicDTO> getWeakTopics(String handle) {
        List<SubmissionDTO> submissions=codeforcesApiClient.getUserSubmissions(handle).getResult();
        Map<String,int[]> tagStats=new HashMap<>();
        for(SubmissionDTO submission:submissions){
            if(submission.getProblem()==null
            ||submission.getProblem().getTags()==null) continue;

            for(String tag:submission.getProblem().getTags()){
                tagStats.putIfAbsent(tag,new int[]{0,0});
                tagStats.get(tag)[0]++;
                if("OK".equals(submission.getVerdict())){
                    tagStats.get(tag)[1]++;
                }
            }
        }
        return tagStats.entrySet().stream()
                .filter(e->e.getValue()[0]>=3)
                .sorted(Comparator.comparingDouble(e ->
                        (double) e.getValue()[1] / e.getValue()[0]))
                .limit(5)
                .map(e->insightMapper.mapToWeakTopicDTO(
                        e.getKey(),
                        e.getValue()[0]
                        ,e.getValue()[1]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationDTO> getRecommendations(String handle) {
        return List.of();
    }

    @Override
    public List<UpsolveDTO> getUpsolveProblems(String handle, int contestId) {
        return List.of();
    }
}
