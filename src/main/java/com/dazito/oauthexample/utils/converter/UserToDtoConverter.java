package com.dazito.oauthexample.utils.converter;

import com.dazito.oauthexample.service.dto.request.AccountDto;
import com.dazito.oauthexample.utils.converter.config.AutoConverter;
import org.springframework.security.core.userdetails.User;

public class UserToDtoConverter extends AutoConverter<User, AccountDto> {
}
