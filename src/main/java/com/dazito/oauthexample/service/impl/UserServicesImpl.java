package com.dazito.oauthexample.service.impl;

import com.dazito.oauthexample.dao.AccountRepository;
import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.UserService;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.response.NameDto;
import com.dazito.oauthexample.service.dto.response.PasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServicesImpl implements UserService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Resource(name = "conversionService")
    ConversionService conversionService;

    @Autowired
    public UserServicesImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AccountDto getCurrentUser(String name) {
        return conversionService.convert(findUserByName(name), AccountDto.class);
    }

    @Override
    public PasswordDto editPassword(String name, String newPassword, String rawOldPassword) {
        AccountEntity accountEntity = findUserByName(name);
        boolean matches = passwordEncoder.matches(rawOldPassword, accountEntity.getPassword());
        if (matches){
            accountEntity.setPassword(passwordEncoder.encode(newPassword));
            accountRepository.saveAndFlush(accountEntity);
        }

        PasswordDto passwordDto = new PasswordDto();
        passwordDto.setPassword(newPassword);
        return passwordDto;
    }

    @Override
    public NameDto editName(String name, String newName) {
        AccountEntity accountEntity = findUserByName(name);
        accountEntity.setUsername(newName);
        accountRepository.saveAndFlush(accountEntity);

        NameDto nameDto = new NameDto();
        nameDto.setUsername(newName);
        return nameDto;
    }

    @Override
    public Collection<NameDto> getAccountsByRole(String role) {
        Collection<AccountEntity> byRole = accountRepository.findByRole(role);
        if (byRole == null)return null;
        List<NameDto> collectionNames = new ArrayList<>();
        for (AccountEntity entity : byRole) {
            NameDto convertedEntity = conversionService.convert(entity, NameDto.class);
            collectionNames.add(convertedEntity);
        }
        return collectionNames;
    }

    private AccountEntity findUserByName(String name){
        if (accountRepository.findByUsername(name).isPresent()){
            return  accountRepository.findByUsername(name).get();
        } else {
            return new AccountEntity();
        }
    }
}
