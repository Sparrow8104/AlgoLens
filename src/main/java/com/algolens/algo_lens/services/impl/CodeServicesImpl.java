package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.config.CfSessionManager;
import com.algolens.algo_lens.dtos.code.*;
import com.algolens.algo_lens.dtos.code.raw.ContestSubmissionDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.exception.CodeFetchException;
import com.algolens.algo_lens.exception.NoCommonContestsException;
import com.algolens.algo_lens.mapper.CodeMapper;
import com.algolens.algo_lens.services.service.CodeServices;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.Patch;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
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
                    "No common contests found between "+handle1+" and "+handle2+
                            " in their last 300 submissions"
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

        List<SubmissionDTO> subs1 = codeforcesApiClient.getUserSubmissions(request.handle1()).getResult();
        List<SubmissionDTO> subs2 = codeforcesApiClient.getUserSubmissions(request.handle2()).getResult();

        Map<String,SubmissionDTO> best1= getBestSubmissionsPerProblem(subs1, request.contestId());
        Map<String,SubmissionDTO> best2= getBestSubmissionsPerProblem(subs2, request.contestId());

        System.out.println("User1 problems: " + best1.keySet());
        System.out.println("User2 problems: " + best2.keySet());

        Set<String> commonIndexes=new HashSet<>(best1.keySet());
        commonIndexes.retainAll(best2.keySet());

        if(commonIndexes.isEmpty()){
            throw new NoCommonContestsException(
                    "No common problems found between "+request.handle1()+" and "
                            +request.handle2()+" in contest "+request.contestId()
            );
        }

        String contestName=codeforcesApiClient
                .getContests().getResult().stream()
                .filter(c->c.getId()==request.contestId())
                .map(CodeforcesContestItemDTO::getName)
                .findFirst()
                .orElse("Contest"+request.contestId());

        List<ProblemCompareDTO> problems=commonIndexes.stream()
                .sorted()
                .map(index->{
                   SubmissionDTO sub1=best1.get(index);
                   SubmissionDTO sub2=best2.get(index);

                    String code1=fetchCode(sub1.getId(), request.contestId());
                    String code2=fetchCode(sub2.getId(),request.contestId());

                    List<DiffDeltaDTO> diff1=computeDiff(code1, code2);
                    List<DiffDeltaDTO> diff2=computeDiff(code2, code1);

                    return ProblemCompareDTO.builder()
                            .index(index)
                            .submission1(codeMapper.mapToSubmissionCodeDTO(
                                    sub1, code1, diff1))
                            .submission2(codeMapper.mapToSubmissionCodeDTO(
                                    sub2, code2, diff2))
                            .build();
                })
                .toList();

        return CodeCompareResponseDTO.builder()
                .contestId(request.contestId())
                .contestName(contestName)
                .problems(problems)
                .build();

    }

    private String fetchCode(Long submissionId,int contestId){
        if (cfSessionManager.hasCodeCached(submissionId)) {
            return cfSessionManager.getCachedCode(submissionId);
        }

        try{
            Thread.sleep(750);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }

        String code=scrape(submissionId,contestId,false);
        cfSessionManager.cacheCode(submissionId,code);
        return code;
    }

    private String scrape(Long submissionId,int contestId,boolean isRetry){
        try{
            String url="https://codeforces.com/contest/"+contestId+"/submission/"+submissionId;

            System.out.println("Fetching URL: "+url);
            Document doc= Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (compatible; AlgoLens/1.0)")
                    .cookies( cfSessionManager.getCookies())
                    .timeout(15000)
                    .get();

            if(!doc.select("form[action='/enter']").isEmpty()) {
                if (isRetry) {
                    throw new CodeFetchException(
                            "Codeforces session expired- re-login failed"
                    );
                }
                System.out.println("Session expired! re-logging");
                cfSessionManager.invalidateSession();
                return scrape(submissionId, contestId, true);
            }
                String code=doc.select("pre#program-source-text").text();

                if(code==null||code.isEmpty()){
                    code=doc.select("pre.prettyprint").text();
                }
                if(code==null||code.isEmpty()){
                    return "Could not fetch code for submission"+submissionId;
                }

                return code;
        }catch(IOException e){
          throw new CodeFetchException(
                  "Failed to scrape submission"+submissionId+": "+e.getMessage());
        }
    }

    private Map<String,SubmissionDTO> getBestSubmissionsPerProblem(
            List<SubmissionDTO> submissions,int contestId
    ){
        Map<String,SubmissionDTO> best=new LinkedHashMap<>();

        submissions.stream()
                .filter(s->s.getProblem()!=null&&
                        s.getProblem().getContestId()==contestId&&
                        s.getProblem().getIndex()!=null
                )
                .forEach(s->{
                    String index=s.getProblem().getIndex();
                    SubmissionDTO current=best.get(index);
                    if(current==null|| !"OK".equalsIgnoreCase(current.getVerdict())
                    &&"OK".equalsIgnoreCase(s.getVerdict())){
                        best.put(index,s);
                    }
                });
        return best;
    }

    private List<DiffDeltaDTO> computeDiff(String code1, String code2) {
        List<String> lines1 = Arrays.asList(code1.split("\n"));
        List<String> lines2 = Arrays.asList(code2.split("\n"));
        Patch<String> patch = DiffUtils.diff(lines1, lines2);
        return codeMapper.mapToDiffDeltas(patch);
    }
}
