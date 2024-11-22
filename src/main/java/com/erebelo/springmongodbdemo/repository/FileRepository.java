package com.erebelo.springmongodbdemo.repository;

import com.erebelo.springmongodbdemo.domain.entity.FileDocument;
import com.erebelo.springmongodbdemo.repository.projection.FileDocumentProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for interacting with FileDocument entities.
 * <p>
 * Using projections to fetch only the file's id and name for efficiency,
 * avoiding the retrieval of unnecessary file data.
 */
@Repository
public interface FileRepository extends MongoRepository<FileDocument, String> {

    Optional<FileDocumentProjection> findByName(String name);

    @Query("SELECT f.id AS id, f.name AS name FROM FileDocument f")
    List<FileDocumentProjection> findAllFileDocumentsProjection();

}
