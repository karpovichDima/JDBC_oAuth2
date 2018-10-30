package com.dazito.oauthexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class Structure extends StorageElementWithChildren{
}
