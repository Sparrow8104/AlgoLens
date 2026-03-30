package com.algolens.algo_lens.utils;

import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromptBuilder {

    public String buildExplainPrompt(
            String code,
            String language,
            List<WeakTopicDTO> weakTopics,
            List<SubmissionDTO> problemSubmissions
    ){
        StringBuilder sb = new StringBuilder();

        sb.append("You are a competitive programming coach. ");
        sb.append("Analyze this ").append(language).append(" solution. \n\n");

        if(!problemSubmissions.isEmpty()){
            sb.append("STUDENT CONTEXT:\n");
            sb.append("This student attempted this problem ")
                    .append(problemSubmissions.size()).append(" time(s).\n");

            long wrongAttempts=problemSubmissions.stream()
                    .filter(s->!"OK".equals(s.getVerdict())).count();
            if (wrongAttempts>0) {
                sb.append("They had ").append(wrongAttempts)
                        .append(" wrong attempt(s) before this solution.\n");
            }
        }
        sb.append("\nCODE TO ANALYZE:\n```").append(language).append("\n");
        sb.append(truncate(code));
        sb.append("\n```\n\n");

        sb.append("Respond in JSON only, no markdown:\n");
        sb.append("{\n");
        sb.append("  \"explanation\": \"clear explanation of what this code does and its approach\",\n");
        sb.append("  \"timeComplexity\": \"Big O time complexity with brief reason\",\n");
        sb.append("  \"spaceComplexity\": \"Big O space complexity with brief reason\",\n");
        sb.append("  \"patternIdentified\": \"algorithm pattern e.g. sliding window, dp, greedy\",\n");
        sb.append("  \"personalisedTips\": [\"tip1 based on their weak areas\", \"tip2\", \"tip3\"]\n");
        sb.append("}");

        return sb.toString();


    }

    private String truncate(String code){
        int maxChars=6000;
        if(code.length()<=maxChars) return code;
        return code.substring(0, maxChars)+"\n// ... truncated";
    }

}
