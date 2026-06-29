package com.algolens.algo_lens.services;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.services.EmailService;
import com.algolens.algo_lens.models.Contest;
import com.algolens.algo_lens.repository.ContestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final TwilioService twilioService;
    private final EmailService emailService;

    // Utilize Java 21's Virtual Threads for lightweight, concurrent blocking I/O tasks.
    private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

    @Scheduled(cron = "0 * * * * *")
    public void dispatchNotifications() {
        long currentSeconds = Instant.now().getEpochSecond();

        long startWindow = currentSeconds + 270;
        long endWindow = currentSeconds + 330;

        List<Contest> upcomingContests = contestRepository.findByIsActiveTrue()
                .stream()
                .filter(c -> c.getStartTimeSeconds() >= startWindow &&
                        c.getStartTimeSeconds() <= endWindow)
                .toList();

        if (!upcomingContests.isEmpty()) {
            List<User> eligibleUsers = userRepository.findByEmailVerifiedTrueAndNotifyBeforeContestTrue();

            for (Contest contest : upcomingContests) {
                log.info("Dispatching notifications for contest: {}", contest.getName());
                for (User user : eligibleUsers) {
                    executorService.submit(() -> {
                        try {
                            String message = String.format("Hello %s, your Codeforces contest '%s' starts in 5 minutes! Good luck!", 
                                    user.getName(), contest.getName());

                            // Try sending email (isolated error handling)
                            try {
                                emailService.sendContestNotificationEmail(user.getEmail(), user.getName(), contest.getName());
                                log.debug("Email notification dispatched successfully to user: {}", user.getEmail());
                            } catch (Exception e) {
                                log.error("Failed to send email notification to user: {}. Error: {}", user.getEmail(), e.getMessage());
                            }

                            // Try sending SMS and Call (isolated error handling)
                            if (user.isPhoneVerified() && user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                                try {
                                    twilioService.sendSms(user.getPhoneNumber(), message);
                                    log.debug("SMS notification dispatched successfully to: {}", user.getPhoneNumber());
                                } catch (Exception e) {
                                    log.error("Failed to send SMS notification to phone: {}. Error: {}", user.getPhoneNumber(), e.getMessage());
                                }

                                try {
                                    twilioService.makeAgenticCall(user.getPhoneNumber(), message);
                                    log.debug("Voice call notification initiated successfully to: {}", user.getPhoneNumber());
                                } catch (Exception e) {
                                    log.error("Failed to initiate voice call notification to phone: {}. Error: {}", user.getPhoneNumber(), e.getMessage());
                                }
                            }
                        } catch (Exception e) {
                            log.error("Unexpected error in async notification thread for user: {}", user.getEmail(), e);
                        }
                    });
                }
            }
        }
    }
}
