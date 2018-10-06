package com.dazito.oauthexample.model;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends StorageElement {
}
