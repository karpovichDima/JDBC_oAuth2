package com.dazito.oauthexample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends StorageElementWithChildren {
}
