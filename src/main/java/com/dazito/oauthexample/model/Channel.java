package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("CHANNEL")
public class Channel extends StorageElement{

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name="storage_parent",
            joinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="parent_id", referencedColumnName="parent_id"))
    private List<StorageElement> storageElements;
}
