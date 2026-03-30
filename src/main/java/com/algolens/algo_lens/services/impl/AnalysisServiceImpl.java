package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.client.GeminiClient;
import com.algolens.algo_lens.dtos.analysis.ExplainRequestDTO;
import com.algolens.algo_lens.dtos.analysis.ExplainResponseDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.exception.AiServiceException;
import com.algolens.algo_lens.services.service.AnalysisService;
import com.algolens.algo_lens.services.service.InsightServices;
import com.algolens.algo_lens.utils.PromptBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisServiceImpl implements AnalysisService {
    private final InsightServices insightServices;
    private final CodeforcesApiClient codeforcesApiClient;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;
    private final PromptBuilder promptBuilder;

    public AnalysisServiceImpl(InsightServices insightServices, CodeforcesApiClient codeforcesApiClient, GeminiClient geminiClient, ObjectMapper objectMapper, PromptBuilder promptBuilder) {
        this.insightServices = insightServices;
        this.codeforcesApiClient = codeforcesApiClient;
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.promptBuilder = promptBuilder;
    }

    @Override
    @Cacheable(value="codeAnalysis",
            key="#request.handle()+'_'+#request.code().hashCode()")
    public ExplainResponseDTO explainCode(ExplainRequestDTO request) {
        List<WeakTopicDTO> weakTopics=insightServices.getWeakTopics(request.handle());

        List<SubmissionDTO> problemSubmissions=List.of();
        if(request.contestId()!=null&&request.problemIndex()!=null){
            problemSubmissions=codeforcesApiClient.getUserSubmissions(
                    request.handle()
            ).getResult()
                    .stream()
                    .filter(s->s.getProblem()!=null
                    &&request.contestId().equals(s.getProblem().getContestId())
                    &&request.problemIndex().equalsIgnoreCase(s.getProblem().getIndex()))
                    .toList();
        }
        String prompt=promptBuilder.buildExplainPrompt(
                request.code(),
                request.language(),
                weakTopics,
                problemSubmissions
        );

        String rawResponse=geminiClient.generate(prompt);
        return parseJson(rawResponse,ExplainResponseDTO.class);
    }

    private<T> T parseJson(String raw,Class<T> type) {
        try{
            String cleaned=raw.replaceAll("(?s)```json\\s*", "")
                    .replaceAll("(?s)```\\s*", "")
                    .trim();
            return objectMapper.readValue(cleaned, type);
        }catch (Exception e){
           log.error("Failed to parse Ai response: {}",raw);
           throw new AiServiceException(
                   "AI returned invalid response format: "+e.getMessage()
           );
        }
    }
}


