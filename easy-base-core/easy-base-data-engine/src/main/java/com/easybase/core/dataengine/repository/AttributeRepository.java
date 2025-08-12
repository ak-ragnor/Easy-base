package com.easybase.core.dataengine.repository;

import com.easybase.core.dataengine.entity.Attribute;
import com.easybase.core.dataengine.entity.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

    Optional<Attribute> findByCollectionAndName(Collection collection, String name);

    Optional<Attribute> findByCollectionIdAndName(UUID collectionId, String name);

    List<Attribute> findByCollection(Collection collection);

    List<Attribute> findByCollectionId(UUID collectionId);

    List<Attribute> findByCollectionAndIsIndexed(Collection collection, Boolean isIndexed);

    List<Attribute> findByCollectionIdAndIsIndexed(UUID collectionId, Boolean isIndexed);

    boolean existsByCollectionAndName(Collection collection, String name);

    boolean existsByCollectionIdAndName(UUID collectionId, String name);
}
