package com.algolens.algo_lens.services;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.userInfo.UserProfileDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserServices {

    UserProfileDTO getUserProfile(String handle);
    Page<ContestDTO> getUserContestHistory(String handle, Pageable pageable);
}
