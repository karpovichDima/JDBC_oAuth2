package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.List;

@Setter
@Getter
@Entity
@DiscriminatorValue("CHANNEL")
public class Channel extends StorageElementWithChildren{
}
