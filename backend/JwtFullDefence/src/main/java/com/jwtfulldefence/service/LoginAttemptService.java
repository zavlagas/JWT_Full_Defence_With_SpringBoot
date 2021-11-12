package com.jwtfulldefence.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.*;

@Service
public class LoginAttemptService {

    private static final int MAX_AMOUNT_NUMBER_OF_ATTEMPTS = 5;

    private static final int ATTEMPT_INCREMENT = 1;

    private LoadingCache<String, Integer> loginAttemptCache;


    public LoginAttemptService() {
        super();
        this.loginAttemptCache = CacheBuilder
                .newBuilder()
                .expireAfterWrite(15, MINUTES)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    @Override
                    public Integer load(String key) throws Exception {
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String username) {
        this.loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username) {
        int attempts = 0;

        try {
            attempts = ATTEMPT_INCREMENT + this.loginAttemptCache.get(username);
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
        loginAttemptCache.put(username, attempts);

    }

    public boolean hasExceedMaxAttempts(String username) {
        boolean mustBeLocked = false;
        try {
            mustBeLocked = this.loginAttemptCache.get(username) >= MAX_AMOUNT_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        }
        return mustBeLocked;
    }

}
