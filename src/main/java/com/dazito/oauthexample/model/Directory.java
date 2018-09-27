package com.dazito.oauthexample.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends StorageElement {
}
