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
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodeforcesSyncService {

    private final ContestRepository contestRepository;
    private final CodeforcesApiClient codeforcesApiClient;

    @Scheduled(fixedRate = 3600000)
    @Transactional(rollbackFor = Exception.class)
    public void syncContestsWithStatusUpdate() {
        log.info("Starting Codeforces Contests Synchronization...");

        CodeforcesContestResponseDTO response = codeforcesApiClient.getContests();

        if (response == null ||
                !"OK".equals(response.getStatus()) ||
                response.getResult() == null) {
            log.warn("Invalid or empty response from Codeforces API. Skipping sync.");
            return;
        }

        Map<Integer, Contest> existingContestMap =
                contestRepository.findAll()
                        .stream()
                        .collect(Collectors.toMap(
                                Contest::getCodeforcesId,
                                Function.identity()
                        ));

        List<Contest> contestsToSave = new ArrayList<>();
        Set<Integer> incomingCodeforcesIds = new HashSet<>();

        for (CodeforcesContestItemDTO dto : response.getResult()) {

            if (dto == null) {
                log.warn("Encountered null contest entry in API response, skipping.");
                continue;
            }

            if ("BEFORE".equals(dto.getPhase()) && dto.getStartTimeSeconds() > 0) {
                incomingCodeforcesIds.add(dto.getId());

                Contest contest = existingContestMap
                        .computeIfAbsent(dto.getId(), id -> new Contest());

                contest.setCodeforcesId(dto.getId());
                contest.setName(dto.getName());
                contest.setStartTimeSeconds(dto.getStartTimeSeconds());
                contest.setType(dto.getType());
                contest.setPhase(dto.getPhase());
                contest.setActive(true);

                contestsToSave.add(contest);
            }
        }

        if (!contestsToSave.isEmpty()) {
            contestRepository.saveAll(contestsToSave);
            log.info("Synced {} upcoming contests.", contestsToSave.size());
        } else {
            log.info("No upcoming contests found in API response. Nothing saved.");
        }

        List<Contest> contestsToDeactivate = existingContestMap.values().stream()
                .filter(Contest::isActive)
                .filter(contest -> !incomingCodeforcesIds.contains(contest.getCodeforcesId()))
                .toList();

        if (!contestsToDeactivate.isEmpty()) {
            contestsToDeactivate.forEach(contest -> contest.setActive(false));
            contestRepository.saveAll(contestsToDeactivate);
            log.info("Marked {} contests as inactive.", contestsToDeactivate.size());
        }

        long thirtyDaysAgo = (System.currentTimeMillis() / 1000) - (30L * 24 * 60 * 60);
        long deletedCount = contestRepository.deleteByActiveFalseAndStartTimeSecondsLessThan(thirtyDaysAgo);

        if (deletedCount > 0) {
            log.info("Deleted {} old finished contests (older than 30 days).", deletedCount);
        }

        log.info("Codeforces sync completed successfully.");
    }
}