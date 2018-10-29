package com.dazito.oauthexample.service;

import com.dazito.oauthexample.service.dto.response.AvatarCreatedDto;
import com.dazito.oauthexample.service.dto.response.AvatarDeletedDto;
import com.dazito.oauthexample.utils.exception.AppException;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AvatarService {


    AvatarCreatedDto createAvatar(Long channelId, MultipartFile file) throws AppException, IOException;

    Resource getAvatar(Long idAvatarOwner) throws AppException, IOException;

    AvatarDeletedDto deleteAvatar(Long idAvatarOwner) throws AppException;
}
