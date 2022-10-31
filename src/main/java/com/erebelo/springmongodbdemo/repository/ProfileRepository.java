package com.erebelo.springmongodbdemo.repository;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<ProfileEntity, String> {

    ProfileEntity findByUserId(String userId);

}
