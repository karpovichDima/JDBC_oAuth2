package com.dazito.oauthexample.model;

import com.dazito.oauthexample.model.type.SomeType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class StorageElement{

    // Lombok

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String name;

    @Column
    private Long parentId;

    @Enumerated(value = EnumType.STRING)
    @Column(updatable = false, insertable = false)
    private SomeType type;

}
