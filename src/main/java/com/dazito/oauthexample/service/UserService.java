package com.dazito.oauthexample.service;

import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void editPassword(String name);
}
