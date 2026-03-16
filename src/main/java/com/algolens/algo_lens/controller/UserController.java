package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.services.CodeforcesServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {


    private  final CodeforcesServices codeforcesServices;
    public UserController(CodeforcesServices codeforcesServices) {{
    this.codeforcesServices = codeforcesServices;}
    }


}
