package com.algolens.algo_lens.services.stats;


import com.algolens.algo_lens.dtos.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.userStatus.SubmissionDTO;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserStatsService {

    public int calculateProblemsSolved(List<SubmissionDTO> submissions) {
        Set<String> solvedProblems =new HashSet<>();
        for(SubmissionDTO submissionDTO:submissions){
            if("OK".equals(submissionDTO.getVerdict())){
                ProblemDTO problem=submissionDTO.getProblem();
                String key=problem.getContestId()+"-"+problem.getIndex();
                solvedProblems.add(key);
            }
        }
        return solvedProblems.size();
    }

    public int calculateContestsParticipated(UserRatingResponseDTO response) {
        return response.getResult().size();
    }

    public int calculateStreakDays(Set<LocalDate> solvedDates) {
        if(solvedDates.isEmpty()) return 0;
        LocalDate current = LocalDate.now();
        int streak=0;
        while(solvedDates.contains(current)){
            streak++;
            current=current.minusDays(1);
        }
        return streak;

    }

    public Set<LocalDate> getSolvedDates(List<SubmissionDTO> submissions) {
        Set<LocalDate> solvedDates = new HashSet<>();
        for(SubmissionDTO submissionDTO:submissions){
            if("OK".equals(submissionDTO.getVerdict())){
                LocalDate solvedDate= Instant.ofEpochSecond(submissionDTO.getCreationTimeSeconds())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                solvedDates.add(solvedDate);
            }
        }
        return solvedDates;

    }

    public LocalDate getLastActiveDate(CodeforcesUserDTO user) {
        return Instant.ofEpochSecond(user.getLastOnlineTimeSeconds())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

    }
}
