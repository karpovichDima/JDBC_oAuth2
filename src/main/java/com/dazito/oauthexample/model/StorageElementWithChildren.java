package com.dazito.oauthexample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public class StorageElementWithChildren extends StorageElement {

//    @OneToMany(targetEntity = StorageElement.class, mappedBy = "parent")
//    @JsonIgnore
//    private List<StorageElement> children;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name="parent_storage_id",
            joinColumns=@JoinColumn(name="parent_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"))
    private List<StorageElement> children;
}
