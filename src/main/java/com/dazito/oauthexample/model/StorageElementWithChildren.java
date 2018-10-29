package com.dazito.oauthexample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@MappedSuperclass
public abstract class StorageElementWithChildren extends StorageElement {

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name="parent_storage_id",
            joinColumns=@JoinColumn(name="parent_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"))
    private List<StorageElement> children;
}
