package com.algolens.algo_lens.repository;

import com.algolens.algo_lens.models.Friend;
import com.algolens.algo_lens.models.UserFriend;
import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFriendRepository extends JpaRepository<UserFriend,Long> {
    List<UserFriend> findByUserHandle(String userHandle);
    Optional<UserFriend> findByUserHandleAndFriendHandle(String userHandle, String friendHandle);
    boolean existsByUserHandleAndFriendHandle(String userHandle,String friendHandle);
    void deleteByUserHandleAndFriendHandle(String userHandle,String friendHandle);


}
