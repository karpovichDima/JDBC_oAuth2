package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@NoArgsConstructor
public class Organization {

    //TODO: dto
    //TODO: converter

    @Id
    @GeneratedValue
    private Integer id;

    @OneToMany(mappedBy = "organization")
    private List<UserEntity> users = new ArrayList<>();
}
