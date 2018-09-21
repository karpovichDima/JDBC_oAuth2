package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.UserRepository;
import com.dazito.oauthexample.model.UserEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.UserDto;
import com.dazito.oauthexample.service.dto.response.NameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServicesImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Autowired
    public UserServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto getCurrentUser(String name) {
        return conversionService.convert(findUserByName(name), UserDto.class);
    }

    @Override
    public PasswordDto editPassword(String name, String newPassword, String rawOldPassword) {
        UserEntity userEntity = findUserByName(name);
        boolean matches = passwordEncoder.matches(rawOldPassword, userEntity.getPassword());
        if (matches){
            userEntity.setPassword(passwordEncoder.encode(newPassword));
            userRepository.saveAndFlush(userEntity);
        }

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword(newPassword);
        return passwordDto;
    }

    @Override
    public NameDto editName(String name, String newName) {
        UserEntity userEntity = findUserByName(name);
        userEntity.setUsername(newName);
        userRepository.saveAndFlush(userEntity);

        NameDto nameDto = new NameDto();
        nameDto.setName(newName);
        return nameDto;
    }

    private UserEntity findUserByName(String name){
        if (userRepository.findByUsername(name).isPresent()){
            return  userRepository.findByUsername(name).get();
        } else {
            return new UserEntity();
        }
    }
}
