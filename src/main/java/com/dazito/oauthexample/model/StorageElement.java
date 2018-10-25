package com.dazito.oauthexample.model;

import com.dazito.oauthexample.model.type.SomeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class StorageElement implements Serializable{

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false, insertable = false)
    private SomeType type;

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @OneToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private AccountEntity owner;

    @Column
    private Long size;

    @ManyToOne(targetEntity = Organization.class)
    @JoinColumn(name="organization_id")
    @JsonIgnore
    private Organization organization;

    @ManyToMany(cascade = CascadeType.DETACH)
    @JoinTable(
            name="parent_storage_id",
            joinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="parent_id", referencedColumnName="id"))
    private List<StorageElement> parents;

    @ManyToMany
    @JoinTable(
            name="channel_account",
            joinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="user_id", referencedColumnName="id"))
    List<AccountEntity> listOwners;


}
