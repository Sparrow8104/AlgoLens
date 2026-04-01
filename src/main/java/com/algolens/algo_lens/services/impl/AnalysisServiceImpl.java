package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.GroqClient;
import com.algolens.algo_lens.dtos.analysis.AiAnalysisResponseDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.services.service.AnalysisService;
import com.algolens.algo_lens.services.service.InsightServices;
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

    public AnalysisServiceImpl(InsightServices insightServices, GroqClient groqClient) {
        this.insightServices=insightServices;
        this.groqClient =groqClient;
    }

    @Override
    @Cacheable(value="aiUpsolve",key="#handle")
    public AiAnalysisResponseDTO analyzeUpsolve(String handle) {
        List<WeakTopicDTO> weakTopics=insightServices.getWeakTopics(handle);
        Map<Integer, List<UpsolveDTO>> upsolveContests=insightServices.getUpsolveContests(handle);

        String prompt=buildPrompt(handle,weakTopics,upsolveContests);
        String result= groqClient.generate(prompt);
        return new AiAnalysisResponseDTO(result);
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
            \nFor each unsolved problem, provide:
            1. What likely went wrong based on the verdict and tags
            2. The specific concept to study
            3. One concrete thing to try differently
            
            Then give one overall recommendation based on the weak topic pattern.
            Keep the tone encouraging but direct. Be specific, not generic.
            """);

        return sb.toString();
    }
}


