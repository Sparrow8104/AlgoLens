package com.algolens.algo_lens.services;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestResponseDTO;
import com.algolens.algo_lens.models.Contest;
import com.algolens.algo_lens.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeforcesSyncService {

    private final ContestRepository contestRepository;
    private final CodeforcesApiClient codeforcesApiClient;

    // Run every 1 hour (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    public void syncContests() {
        log.info("Starting Codeforces Contests Synchronization...");
        try {
            CodeforcesContestResponseDTO response = codeforcesApiClient.getContests();

            if (response != null && "OK".equals(response.getStatus()) && response.getResult() != null) {
                List<Contest> contestsToSave = new ArrayList<>();
                for (CodeforcesContestItemDTO dto : response.getResult()) {
                    // Only process BEFORE phase contests for notifications
                    if ("BEFORE".equals(dto.getPhase()) && dto.getStartTimeSeconds() > 0) {
                        Contest contest = contestRepository.findByCodeforcesId(dto.getId())
                                .orElse(new Contest());

                        contest.setCodeforcesId(dto.getId());
                        contest.setName(dto.getName());
                        contest.setStartTimeSeconds((long) dto.getStartTimeSeconds());
                        contest.setType(dto.getType());
                        contest.setPhase(dto.getPhase());

                        contestsToSave.add(contest);
                    }
                }

                if (!contestsToSave.isEmpty()) {
                    contestRepository.saveAll(contestsToSave);
                    log.info("Successfully synced {} upcoming contests from Codeforces", contestsToSave.size());
                }
            } else {
                log.warn("Failed to fetch properly formatted response from Codeforces API");
            }
        } catch (Exception e) {
            log.error("Error occurred while syncing contests: {}", e.getMessage());
        }
    }
}
