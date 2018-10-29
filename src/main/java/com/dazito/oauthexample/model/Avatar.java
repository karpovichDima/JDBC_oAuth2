package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@DiscriminatorValue("AVATAR")
public class Avatar extends FileElement{
}


