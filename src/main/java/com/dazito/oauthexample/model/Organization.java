package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    @Column(name = "org_name")
    private String organizationName;

    @Column
    private String salary;

    @OneToMany(mappedBy = "organization")
    private List<AccountEntity> users;
}
