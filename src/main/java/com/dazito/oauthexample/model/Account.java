package com.dazito.oauthexample.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @NotNull
    @GeneratedValue
    private Integer id;
    @NotNull
    @Column(name = "username", length = 128)
    private String username;
    @NotNull
    @Column(name = "password", length = 128)
    private String password;

    Account(String username, String password){
        this.username = username;
        this.password = password;
    }

}
