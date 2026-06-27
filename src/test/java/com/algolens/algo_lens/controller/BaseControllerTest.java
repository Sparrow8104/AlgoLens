package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.auth.services.AuthFilterService;
import com.algolens.algo_lens.auth.services.JwtService;
import com.algolens.algo_lens.auth.config.SecurityConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@Import({SecurityConfiguration.class, AuthFilterService.class})
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected JwtService jwtService;

    @MockitoBean
    protected AuthenticationProvider authenticationProvider;

    @MockitoBean
    protected UserDetailsService userDetailsService;
}
