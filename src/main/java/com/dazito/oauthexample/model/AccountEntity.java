package com.dazito.oauthexample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_entity")
public class AccountEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name", length = 128)
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @Column(name = "role", length = 128)
    private String role;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="organization_id")
    private Organization organization;

    AccountEntity(String username, String password){
        this.username = username;
        this.password = password;
    }

}
