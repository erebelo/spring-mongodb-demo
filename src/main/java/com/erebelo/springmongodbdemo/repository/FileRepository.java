package com.erebelo.springmongodbdemo.repository;

import com.erebelo.springmongodbdemo.domain.entity.FileEntity;
import com.erebelo.springmongodbdemo.repository.projection.FileEntityProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for interacting with File entities. Using projections to fetch
 * only the file's id and name for efficiency, avoiding the retrieval of
 * unnecessary file data.
 */
@Repository
public interface FileRepository extends MongoRepository<FileEntity, String> {

    Optional<FileEntityProjection> findByName(String name);

    @Query("SELECT f.id AS id, f.name AS name FROM FileEntity f")
    List<FileEntityProjection> findAllFileEntityProjection();

}
