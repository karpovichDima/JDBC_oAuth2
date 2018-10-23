package com.dazito.oauthexample.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends StorageElementWithChildren {
}
