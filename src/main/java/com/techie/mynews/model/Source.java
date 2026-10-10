package com.techie.mynews.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity(name = "sources")
public class Source extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "external_id")
    private String externalId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "url")
    private String url;
    @Column(name = "category")
    private String category;
    @Column(name = "language")
    private String language;
    @Column(name = "country")
    private String country;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "source")
    private Set<UserSource> userSources = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Source source)) return false;

        return Objects.equals(source.getExternalId(), this.getExternalId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getExternalId());
    }
}
