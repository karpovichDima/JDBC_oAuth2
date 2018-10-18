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

    @ManyToMany
    @JoinTable(
            name="channel_account",
            joinColumns=@JoinColumn(name="channel_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="id"))
    List<AccountEntity> accountEntityList;

    @ManyToMany
    @JoinTable(
            name="storage_parent",
            joinColumns=@JoinColumn(name="channel_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"))
    List<StorageElement> storageElementList;
}
