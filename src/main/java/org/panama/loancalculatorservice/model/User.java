package org.panama.loancalculatorservice.model;

import lombok.Builder;

@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    private long id;
    private String name;
    private String author;
    private int version;
}
