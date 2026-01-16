package com.techie.mynews.repository;

import com.techie.mynews.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SourceRepository extends JpaRepository<Source,Integer>, JpaSpecificationExecutor<Source> {
    @NativeQuery("""
            SELECT DISTINCT s.external_id
            FROM sources s
            """)
    List<String> findUniqueExternalIds();

    Source findSourceByExternalId(String externalId);
}
