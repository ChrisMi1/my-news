package com.techie.mynews.repository;

import com.techie.mynews.model.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface SourceRepository extends JpaRepository<Source,Integer>, JpaSpecificationExecutor<Source> {
    @Query("""
    SELECT DISTINCT s.externalId 
    FROM Source s
    """)
    List<String> findUniqueExternalIds();

    Source findSourceByExternalId(String externalId);

    @Query("SELECT s FROM Source s WHERE s.externalId IN (:externalIds)")
    Set<Source> findByExternalIds(@Param("externalIds") Set<String> externalIds);
}
