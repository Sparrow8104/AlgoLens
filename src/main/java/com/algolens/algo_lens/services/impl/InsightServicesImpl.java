package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.mapper.InsightMapper;
import com.algolens.algo_lens.services.service.InsightServices;
import org.springframework.stereotype.Service;

import java.util.*;
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
        Integer rating=codeforcesApiClient.getUserInfo(handle)
                .getResult().getFirst().getRating();

        if(rating==null) rating=800;
        List<WeakTopicDTO> weakTopics=getWeakTopics(handle);
        if(weakTopics.isEmpty()) return List.of();

        Set<String> solvedProblems=getSolvedProblemKeys(handle);

        int finalUserRating=rating;
        List<RecommendationDTO> recommendations=new ArrayList<>();
        for(WeakTopicDTO weakTopic:weakTopics){
            List<ProblemDTO> problems=codeforcesApiClient.
                    getProblemsByTag(weakTopic.tag()).getResult().getProblems();

            if(problems==null || problems.isEmpty()) continue;

            problems.stream()
                    .filter(p-> p.getRating()!=null
                                    &&p.getRating()>=finalUserRating
                            &&p.getRating()<=finalUserRating+300
                            &&!solvedProblems.contains(problemKey(p)))
                    .limit(3)
                    .map(insightMapper::mapToRecommendationDTO)
                    .forEach(recommendations::add);
        }
        return recommendations;
    }

    @Override
    public List<UpsolveDTO> getUpsolveProblems(String handle, int contestId) {
        return List.of();
    }

    private String problemKey(ProblemDTO problem) {
        return problem.getContestId()+"_"+problem.getIndex();
    }

    private Set<String> getSolvedProblemKeys(String handle){{
    return codeforcesApiClient.
    getUserSubmissions(handle).getResult().stream()
            .filter(s->"OK".equals(s.getVerdict())
            &&s.getProblem()!=null)
            .map(s-> problemKey(s.getProblem()))
            .collect(Collectors.toSet());}
    }
}
