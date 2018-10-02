package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@DiscriminatorValue("FILE")
public class FileEntity extends StorageElement {

    @Column(unique = true)
    private String uuid;

    @Column
    private String extension;

    @Column
    private Long size;

    @ManyToOne(targetEntity = AccountEntity.class)
    @JoinColumn(name="user_id")
    private AccountEntity owner;
}
