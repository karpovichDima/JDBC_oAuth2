package com.dazito.oauthexample.model;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class AccountEntity implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "name", length = 128)
    private String username;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", length = 128)
    private UserRole role;

    @ManyToOne(targetEntity = Organization.class)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    private Boolean isActivated;

    private String email;

    @OneToMany
    private List<FileEntity> files;

    @Column(name = "uuid", length = 128)
    private String uuid;

    @Column(name = "token_end_date", length = 128)
    private Timestamp tokenEndDate;

    @ManyToMany
    @JoinTable(
            name = "channel_account",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "storage_id", referencedColumnName = "id"))
    private List<Channel> channelList;

    @OneToMany
    private List<Collection> collections;

    AccountEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }
}


