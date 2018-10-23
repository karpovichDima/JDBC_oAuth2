package com.dazito.oauthexample.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("CONTENT")
public class Content extends StorageElementWithChildren {
    private String root;
}
