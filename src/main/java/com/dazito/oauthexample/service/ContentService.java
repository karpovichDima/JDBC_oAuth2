package com.dazito.oauthexample.service;

import com.dazito.oauthexample.model.AccountEntity;
import com.dazito.oauthexample.model.StorageElement;
import com.dazito.oauthexample.service.dto.request.ContentUpdateDto;
import com.dazito.oauthexample.service.dto.response.ContentUpdatedDto;

public interface ContentService {

    ContentUpdatedDto updateContent(ContentUpdateDto contentDto);

    boolean filePermissionsCheck(AccountEntity currentUser, StorageElement foundContent);

    void deleteContent(Long id);
}
