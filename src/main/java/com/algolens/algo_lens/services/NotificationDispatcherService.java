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

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatcherService {

    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final TwilioService twilioService;
    private final EmailService emailService;

    // Run every minute
    @Scheduled(cron = "0 * * * * *")
    public void dispatchNotifications() {
        long currentSeconds = Instant.now().getEpochSecond();
        // We look for contests starting in about 5 minutes (300 seconds).
        // Since this runs every 60s, a window from +270s to +330s ensures we catch the contest ONCE for the upcoming 5th minute.
        long startWindow = currentSeconds + 270;
        long endWindow = currentSeconds + 330;

        List<Contest> upcomingContests = contestRepository.findByStartTimeSecondsBetween(startWindow, endWindow);

        if (!upcomingContests.isEmpty()) {
            List<User> eligibleUsers = userRepository.findByEmailVerifiedTrueAndNotifyBeforeContestTrue();

            for (Contest contest : upcomingContests) {
                log.info("Dispatching notifications for contest: {}", contest.getName());
                for (User user : eligibleUsers) {
                    
                    String message = String.format("Hello %s, your Codeforces contest '%s' starts in 5 minutes! Good luck!", 
                            user.getName(), contest.getName());
                            
                    // Send Email universally based on notifyBeforeContest flag
                    emailService.sendContestNotificationEmail(user.getEmail(), user.getName(), contest.getName());

                    // Send SMS and Agentic Call ONLY if phone verified
                    if (user.isPhoneVerified() && user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                        twilioService.sendSms(user.getPhoneNumber(), message);
                        twilioService.makeAgenticCall(user.getPhoneNumber(), message);
                    }
                }
            }
        }
    }
}
