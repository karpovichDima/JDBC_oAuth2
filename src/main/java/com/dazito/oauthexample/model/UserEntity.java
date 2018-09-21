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
@Table(name = "user_entity")
public class UserEntity {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "username", length = 128)
    private String username;

    @Column(name = "password", length = 128)
    private String password;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="organization_id")
    private Organization organization;

    UserEntity(String username, String password){
        this.username = username;
        this.password = password;
    }

}
