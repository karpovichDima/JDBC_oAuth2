package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
public class Organization implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "name")
    private String organizationName;

    @Column
    private String salary;

    @OneToMany(mappedBy = "organization")
    private List<AccountEntity> users;
}
