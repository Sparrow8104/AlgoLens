package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;

import java.util.List;
import java.util.Map;

public interface InsightServices {
    List<WeakTopicDTO> getWeakTopics(String handle);
    List<RecommendationDTO> getRecommendations(String handle);
    Map<Integer,List<UpsolveDTO>> getUpsolveContests(String handle);
}
