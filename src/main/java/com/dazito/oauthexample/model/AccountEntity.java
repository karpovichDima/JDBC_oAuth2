package com.dazito.oauthexample.model;

import com.dazito.oauthexample.model.type.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class AccountEntity{

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name", length = 128)
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role", length = 128)
    private UserRole role;

    @ManyToOne(targetEntity = Organization.class)
    @JoinColumn(name="organization_id")
    private Organization organization;

    private Boolean isActivated;

    private String email;

    @OneToMany
    private List<FileEntity> files;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="content")
    private Content content;

    @Column(name = "uuid", length = 128)
    private String uuid;

    @Column(name = "token_end_date", length = 128)
    private Timestamp tokenEndDate;

    AccountEntity(String username, String password){
        this.username = username;
        this.password = password;
    }
}


