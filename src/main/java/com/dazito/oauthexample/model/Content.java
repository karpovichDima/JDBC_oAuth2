package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("CONTENT")
public class Content extends StorageElement {
    private String root;
}
