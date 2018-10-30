package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Setter
@Getter
@Entity
@DiscriminatorValue("CONTENT")
public class Content extends StorageElementWithChildren {
    private String root;
}
