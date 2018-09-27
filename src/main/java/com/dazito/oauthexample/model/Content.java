package com.dazito.oauthexample.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("CONTENT")
public class Content extends StorageElement {
}
