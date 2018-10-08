package com.dazito.oauthexample.model;

import com.dazito.oauthexample.model.type.SomeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class StorageElement{

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @OneToOne
    @JoinColumn(name="parent_id")
    private StorageElement parentId;

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false, insertable = false)
    private SomeType type;

    @OneToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private AccountEntity owner;

    @Column
    private Long size;
}
