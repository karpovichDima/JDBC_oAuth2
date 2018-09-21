package com.dazito.oauthexample.utils.converter;

import com.dazito.oauthexample.model.UserEntity;
import com.dazito.oauthexample.service.dto.request.UserDto;
import com.dazito.oauthexample.utils.converter.config.AutoConverter;

public class AccountEntityToDtoConverter extends AutoConverter<UserEntity, UserDto> {
}
