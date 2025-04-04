package com.erebelo.springmongodbdemo.repository;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends MongoRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByUserId(String userId);

}
