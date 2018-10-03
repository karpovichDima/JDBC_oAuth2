package com.dazito.oauthexample.service.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
public class FileUploadResponse implements Serializable {

    private final static long serialVersionUID = 57764567;

    private String referenceToDownloadFile;
    private String name;
    private Long size;
}
