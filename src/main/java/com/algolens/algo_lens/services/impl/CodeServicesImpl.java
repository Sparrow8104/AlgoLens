package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.config.CfSessionManager;
import com.algolens.algo_lens.dtos.code.CodeCompareRequestDTO;
import com.algolens.algo_lens.dtos.code.CodeCompareResponseDTO;
import com.algolens.algo_lens.dtos.code.CommonContestDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.exception.NoCommonContestsException;
import com.algolens.algo_lens.mapper.CodeMapper;
import com.algolens.algo_lens.services.service.CodeServices;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CodeServicesImpl implements CodeServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final CfSessionManager cfSessionManager;
    private final CodeMapper codeMapper;

    public CodeServicesImpl(CodeforcesApiClient codeforcesApiClient, CfSessionManager cfSessionManager, CodeMapper codeMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.cfSessionManager = cfSessionManager;
        this.codeMapper = codeMapper;
    }

    @Override
    public List<CommonContestDTO> getCommonContests(String handle1, String handle2) {
        List<SubmissionDTO> submissions1=codeforcesApiClient.getUserSubmissions(handle1).getResult();
        List<SubmissionDTO> submissions2=codeforcesApiClient.getUserSubmissions(handle2).getResult();

        List<SubmissionDTO> recent1=submissions1.subList(0,Math.min(300,submissions1.size()-1));
        List<SubmissionDTO> recent2=submissions2.subList(0,Math.min(300,submissions2.size()-1));

        Set<Integer> contestIds1=recent1.stream()
                .filter(s->s.getProblem()!=null
                &&s.getProblem().getContestId()!=null
                &&s.getProblem().getContestId()<100000)
                .map(s->s.getProblem().getContestId())
                .collect(Collectors.toSet());

        Set<Integer> contestIds2=recent2.stream()
                .filter(s->s.getProblem()!=null
                        &&s.getProblem().getContestId()!=null
                        &&s.getProblem().getContestId()<100000)
                .map(s->s.getProblem().getContestId())
                .collect(Collectors.toSet());

        contestIds1.retainAll(contestIds2);

        if(contestIds1.isEmpty()){
            throw new NoCommonContestsException(
                    "No common contests found between"+handle1+" and "+handle2+
                            "in their last 300 submissions"
            );
        }

        Map<Integer,String> contestNames=codeforcesApiClient
                .getContests().getResult().stream()
                .collect(Collectors.toMap(
                        CodeforcesContestItemDTO::getId,
                        CodeforcesContestItemDTO::getName,
                        (a,b) -> a
                ));


        return contestIds1.stream()
                .sorted(Comparator.reverseOrder())
                .limit(5)
                .map(id->codeMapper.mapToCommonContestDTO(
                        id,
                        contestNames.getOrDefault(id,"Contest "+id)
                )).collect(Collectors.toList());
    }

    @Override
    public CodeCompareResponseDTO compareCode(CodeCompareRequestDTO request) {
        return null;
    }
}
