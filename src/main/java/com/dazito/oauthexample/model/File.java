package com.dazito.oauthexample.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("FILE")
public class File extends StorageElement {
}
