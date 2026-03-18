package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.userInfo.UserProfileDTO;
import com.algolens.algo_lens.services.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private  final UserServices codeforcesServices;
    public UserController(UserServices codeforcesServices) {{
    this.codeforcesServices = codeforcesServices;}
    }

    @GetMapping("/{handle}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String handle) {
        UserProfileDTO userProfile = codeforcesServices.getUserProfile(handle);
        if(userProfile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userProfile);
    }



}
