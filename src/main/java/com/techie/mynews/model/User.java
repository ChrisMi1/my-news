package com.techie.mynews.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.*;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserSource> userSources = new HashSet<>();

    public void addSource(Source source) {
        UserSource userSource = new UserSource();
        userSource.setUser(this);
        userSource.setSource(source);

        this.userSources.add(userSource);
    }

    public void removeSource(Source source) {
        this.userSources.removeIf(us -> us.getSource().equals(source));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof User user)) return false;

        return Objects.equals(user.getEmail(), this.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail());
    }
}
