package com.dazito.oauthexample.utils;

import com.dazito.oauthexample.dao.UserRepository;
import com.dazito.oauthexample.model.Account;
import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.service.dto.request.EditNameDto;
import com.dazito.oauthexample.service.dto.request.EditPasswordDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;


public class ConverterAccount {

    @Autowired
    private static UserRepository userRepository;

    public static EditPasswordDto mapAccountToPasswordDto(Account account) {
        EditPasswordDto editPasswordDto = new EditPasswordDto();
        editPasswordDto.setNewPassword(account.getPassword());
        return editPasswordDto;
    }
    public static Account mapPasswordDtoToAccount(EditPasswordDto editPasswordDto) {
        Account account = userRepository.findByUsername(findOutNameUser()).get();
        account.setPassword(editPasswordDto.getNewPassword());
        return account;
    }

    public static AccountDto mapAccountEntityToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setPassword(account.getPassword());
        accountDto.setUsername(account.getUsername());
        return accountDto;
    }
    public static Account mapAccountDtoToEntity(AccountDto accountDto) {
        Account account = new Account();
        account.setId(accountDto.getId());
        account.setPassword(accountDto.getPassword());
        account.setUsername(accountDto.getUsername());
        return account;
    }

    public static EditNameDto mapAccountToNameDto(Account account) {
        EditNameDto editNameDto = new EditNameDto();
        editNameDto.setNewName(account.getUsername());
        return editNameDto;
    }
    public static Account mapNameDtoToAccount(EditNameDto editNameDto) {
        Account account = userRepository.findByUsername(findOutNameUser()).get();
        account.setUsername(editNameDto.getNewName());
        return account;
    }

    // get name of the current user
    private static String findOutNameUser(){return SecurityContextHolder.getContext().getAuthentication().getName();}

}