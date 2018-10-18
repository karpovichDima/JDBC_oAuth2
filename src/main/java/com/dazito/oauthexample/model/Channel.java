package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Table(name = "channel")
@Entity
public class Channel {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String channelName;

    @ManyToOne
    @JoinColumn(name = "owner")
    private AccountEntity owner;

    @ManyToMany(cascade = CascadeType.ALL)
    List<AccountEntity> accountEntityList;

    @ManyToMany(cascade = CascadeType.ALL)
    List<FileEntity> fileEntityList;
}
