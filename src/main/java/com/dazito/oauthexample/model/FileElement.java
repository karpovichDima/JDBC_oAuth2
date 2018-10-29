package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class FileElement extends StorageElement {
    @Column(unique = true)
    private String uuid;
    @Column
    private String extension;
}
