package com.dazito.oauthexample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.OneToMany;
import java.util.List;

@Getter
@Setter
public class StorageElementWithChildren extends StorageElement {

    @OneToMany(targetEntity = StorageElement.class, mappedBy = "parent")
    @JsonIgnore
    private List<StorageElement> children;
}
