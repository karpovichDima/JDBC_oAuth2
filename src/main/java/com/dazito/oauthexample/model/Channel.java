package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("CHANNEL")
public class Channel extends StorageElementWithChildren{

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(
            name="storage_parent",
            joinColumns=@JoinColumn(name="parent_id", referencedColumnName="id"),
            inverseJoinColumns=@JoinColumn(name="storage_id", referencedColumnName="id"))
    private List<StorageElement> children;
}
