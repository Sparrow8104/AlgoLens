package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;

import java.util.List;

public interface InsightServices {
    List<WeakTopicDTO> getWeakTopics(String handle);
    List<RecommendationDTO> getRecommendations(String handle);
    List<UpsolveDTO> getUpsolveProblems(String handle,int contestId);
}
