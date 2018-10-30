package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@DiscriminatorValue("COLLECTION")
public class Collection extends StorageElementWithChildren {
}
