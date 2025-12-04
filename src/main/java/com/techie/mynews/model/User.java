package com.techie.mynews.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", insertable = false, updatable = false)
    private String id;

    @Column(name = "user_email",length = 500)
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_sources", joinColumns = {
            @JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
            @JoinColumn(name = "source_id", referencedColumnName = "id") })
    private List<Source> sources;
}
