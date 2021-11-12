package com.jwtfulldefence.listener;


import com.jwtfulldefence.model.User;
import com.jwtfulldefence.security.UserPrincipal;
import com.jwtfulldefence.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationSuccessListener {


    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationSuccessListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }


    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) throws ExecutionException {

        if (event.getAuthentication().getPrincipal() instanceof UserPrincipal) {
            UserPrincipal user = (UserPrincipal) event.getAuthentication().getPrincipal();
            this.loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }


}
