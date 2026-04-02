package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.GroqClient;
import com.algolens.algo_lens.dtos.analysis.AiAnalysisResponseDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.services.service.AnalysisService;
import com.algolens.algo_lens.services.service.InsightServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final InsightServices insightServices;
    private final GroqClient groqClient;
    private final ObjectMapper objectMapper;

    public AnalysisServiceImpl(InsightServices insightServices, GroqClient groqClient, ObjectMapper objectMapper) {
        this.insightServices=insightServices;
        this.groqClient =groqClient;
        this.objectMapper = objectMapper;
    }

    @Override
    @Cacheable(value="aiUpsolve",key="#handle")
    public AiAnalysisResponseDTO analyzeUpsolve(String handle) {
        List<WeakTopicDTO> weakTopics = insightServices.getWeakTopics(handle);
        Map<Integer, List<UpsolveDTO>> upsolveContests = insightServices.getUpsolveContests(handle);

        String prompt = buildPrompt(handle, weakTopics, upsolveContests);
        String result = groqClient.generate(prompt);

        try {
            String cleaned = result.replaceAll("(?s)```json\\s*|```", "").trim();
            log.info("Cleaned AI response: {}", cleaned);
            return objectMapper.readValue(cleaned, AiAnalysisResponseDTO.class);

        } catch (Exception e) {
            log.error("Failed to parse AI response for handle: {}. Raw response: {}", handle, result, e);
            throw new RuntimeException("Failed to parse AI analysis response", e);
        }
    }

    private String buildPrompt(
            String handle,
            List<WeakTopicDTO> weakTopics,
            Map<Integer,List<UpsolveDTO>> upsolveContests
    ) {
        StringBuilder sb=new StringBuilder();

        sb.append("You are a competitive programming coach.\n\n");
        sb.append("Analyze this user's recent contest performance and give specific, actionable advice.\n\n");

        sb.append("Handle: ").append(handle).append("\n\n");

        sb.append("Weak topics (by AC rate):\n");
        for (WeakTopicDTO topic : weakTopics) {
            sb.append(String.format("- %s: %.0f%% AC rate (%d/%d solved)\n",
                    topic.tag(),
                    topic.acRate() * 100,
                    topic.solvedCount(),
                    topic.totalAttempts()));
        }

        sb.append("\nRecent contests with unsolved problems:\n");
        for (Map.Entry<Integer, List<UpsolveDTO>> entry : upsolveContests.entrySet()) {
            sb.append("Contest ").append(entry.getKey()).append(":\n");
            for (UpsolveDTO problem:entry.getValue()) {
                sb.append(String.format(
                        "  - Problem %s: %s (rating: %d, tags: %s, best verdict: %s)\n",
                        problem.index(),
                        problem.name(),
                        problem.rating()!=null?problem.rating() : 0,
                        String.join(", ", problem.tags()),
                        problem.bestVerdict()
                ));
            }
        }

        sb.append("""
IMPORTANT RULES:
- Return ONLY raw JSON
- DO NOT use markdown (no ```json)
- DO NOT rename fields
- Use EXACT field names:

problemAnalyses
overallRecommendation

If you change field names, the system will break.

Example:
{
  "problemAnalyses": [
    {
      "contestId": 123,
      "problemIndex": "A",
      "problemName": "Example",
      "likelyIssue": "Wrong approach",
      "conceptToStudy": "Binary Search",
      "actionableTip": "Practice lower_bound problems"
    }
  ],
  "overallRecommendation": "Focus on weak topics"
}
""");
        return sb.toString();
    }
}


