package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.friend.*;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.exception.ExternalApiException;
import com.algolens.algo_lens.mapper.FriendMapper;
import com.algolens.algo_lens.models.UserFriend;
import com.algolens.algo_lens.repository.UserFriendRepository;
import com.algolens.algo_lens.services.service.FriendServices;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FriendServicecImpl implements FriendServices {

    private final UserFriendRepository userFriendRepository;
    private final CodeforcesApiClient codeforcesApiClient;
    private final FriendMapper friendMapper;

    public FriendServicecImpl(UserFriendRepository userFriendRepository, CodeforcesApiClient codeforcesApiClient, FriendMapper friendMapper) {
        this.userFriendRepository = userFriendRepository;
        this.codeforcesApiClient = codeforcesApiClient;
        this.friendMapper = friendMapper;
    }

    @Override
    @Transactional
    public void addFriend(String userHandle, String friendHandle) {
     if(userHandle.equalsIgnoreCase(friendHandle)) {
         throw new ExternalApiException("Cannot add yourself as a friend");
     }

     if(userFriendRepository.existsByUserHandleAndFriendHandle(
             userHandle, friendHandle)) {
         throw new ExternalApiException("Friend already added");
     }

     userFriendRepository.save(new UserFriend(userHandle, friendHandle));
    }

    @Override
    @Transactional
    public void removeFriend(String userHandle, String friendHandle) {
      if(!userFriendRepository.existsByUserHandleAndFriendHandle(userHandle, friendHandle)) {
          throw new ExternalApiException("Friend not found");
      }
      userFriendRepository.deleteByUserHandleAndFriendHandle(userHandle, friendHandle);
    }

    @Override
    public List<FriendDTO> getFriends(String handle) {
        List<String> friendHandles = getFriendHandles(handle);
        if(friendHandles.isEmpty()) { return List.of(); }
        return friendHandles.stream()
                .map(friendHandle-> {
                    CodeforcesUserDTO user = codeforcesApiClient.
                            getUserInfo(friendHandle).getResult().getFirst();
                    int contests = codeforcesApiClient
                            .getUserRatings(friendHandle).getResult().size();
                    return friendMapper.mapToFriendDTO(user, contests);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaderboardEntryDTO> getLeaderboard(String handle) {
        List<String> allHandles = new ArrayList<>(getFriendHandles(handle));
        allHandles.add(handle);

        List<CodeforcesUserDTO> users=allHandles.stream()
                .map(h->codeforcesApiClient
                        .getUserInfo(h).getResult().getFirst())
                .sorted(Comparator.comparing(u->
                        u.getRating()==null?0:-u.getRating()))
                .toList();
        List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
        for(int i=0;i<users.size();i++) {
            leaderboard.add(friendMapper.mapToLeaderboardEntryDTO(
                    i+1,users.get(i)
            ));
        }
        return leaderboard;
    }

    //Pagination is required

    @Override
    public List<UnsolvedByMeDTO> getUnsolvedByMe(String handle) {
        List<String> friendHandles = getFriendHandles(handle);
        if (friendHandles.isEmpty()) return List.of();

        Set<String> mySolvedKeys=getSolvedProblemKeys(handle);

        Map<String,List<String>> problemSolvedByFriends=new HashMap<>();
        Map<String,ProblemDTO> problemDetails=new HashMap<>();

        for(String friendHandle:friendHandles) {
            List<SubmissionDTO> friendSubmissions=codeforcesApiClient.getUserSubmissions(friendHandle).getResult();

            friendSubmissions.stream()
                    .filter(s->"OK".equals(s.getVerdict())
                    &&s.getProblem()!=null)
                    .forEach(s->{
                        String key=problemKey(s.getProblem());
                        if(!mySolvedKeys.contains(key)) {
                            problemSolvedByFriends
                                    .computeIfAbsent(key,k->new ArrayList<>())
                                    .add(friendHandle);
                            problemDetails.put(key,s.getProblem());
                        }
                    });
        }

        return problemSolvedByFriends.entrySet().stream()
                .map(e->{
                    ProblemDTO problem=problemDetails.get(e.getKey());
                    return UnsolvedByMeDTO.builder()
                            .contestId(problem.getContestId())
                            .index(problem.getIndex())
                            .name(problem.getName())
                            .rating(problem.getRating())
                            .tags(problem.getTags())
                            .solvedByFriends(e.getValue())
                            .build();
                })
                .sorted(Comparator.comparingInt(u->
                        u.rating()==null?0:u.rating()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ContestOverlapDTO> getContestOverlap(String handle, int contestId) {
        return List.of();
    }

    @Override
    public List<StreakCompareDTO> getStreakComparison(String handle) {
        return List.of();
    }

    private List<String> getFriendHandles(String handle) {
        return userFriendRepository.findByUserHandle(handle)
                .stream()
                .map(UserFriend::getFriendHandle)
                .collect(Collectors.toList());
    }

    private Set<String> getSolvedProblemKeys(String handle) {
        return codeforcesApiClient
                .getUserSubmissions(handle).getResult().stream()
                .filter(s -> "OK".equals(s.getVerdict())
                        && s.getProblem() != null)
                .map(s -> problemKey(s.getProblem()))
                .collect(Collectors.toSet());
    }
    private String problemKey(ProblemDTO problem) {
        return problem.getContestId() + "_" + problem.getIndex();
    }
}