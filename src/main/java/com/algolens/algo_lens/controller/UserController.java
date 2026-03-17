package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.services.UserServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private  final UserServices codeforcesServices;
    public UserController(UserServices codeforcesServices) {{
    this.codeforcesServices = codeforcesServices;}
    }


}
