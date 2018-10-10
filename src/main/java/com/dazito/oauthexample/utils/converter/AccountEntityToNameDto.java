package com.dazito.oauthexample.utils.converter;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.service.dto.response.EditedEmailNameDto;
import com.dazito.oauthexample.utils.converter.config.AutoConverter;

public class AccountEntityToNameDto extends AutoConverter<AccountEntity, EditedEmailNameDto> {
}
