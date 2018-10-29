package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
@DiscriminatorValue("FILE")
public class FileEntity extends FileElement {
}
