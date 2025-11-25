package com.techie.mynews.model;

import com.techie.mynews.utils.AuthUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public class BaseEntity {
    @Column(name = "rowversion")
    @Version
    private Integer rowVersion;
    @Column(name = "inserted_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "inserted_by", updatable = false)
    private String createdBy;
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
    @Column(name = "modified_by")
    private String modifiedBy;


    @PrePersist
    void createdAt() {
        createdAt =  LocalDateTime.now();
        modifiedAt = createdAt;
        createdBy = AuthUtils.getActionerName();
    }

    @PreUpdate
    void updateAt() {
        modifiedAt = LocalDateTime.now();
        modifiedBy = AuthUtils.getActionerName();
    }

}
