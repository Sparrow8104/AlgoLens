package com.algolens.algo_lens.repository;

import com.algolens.algo_lens.models.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Long> {
    List<Friend> findByUserHandle(String userHandle);
    boolean existsByUserHandleAndFriendHandle(String userHandle,String friendHandle);
    void deleteByUserHandleAndFriendHandle(String userHandle,String friendHandle);


}
