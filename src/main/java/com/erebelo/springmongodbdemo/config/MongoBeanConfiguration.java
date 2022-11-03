package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.history.MongoHistoryEventListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableMongoRepositories(basePackages = "com.erebelo.springmongodbdemo")
public class MongoBeanConfiguration {

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        // Enable through @Transactional annotation rollback the REST operations if any exception occurs
        return new MongoTransactionManager(dbFactory);
    }

    @Bean
    public MongoHistoryEventListener mongoEventListener() {
        // Enable the mongodb event listener to manipulate the entity/document before/after persist it
        return new MongoHistoryEventListener();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener(final LocalValidatorFactoryBean factory) {
        // Enable the entity validation before persist it
        return new ValidatingMongoEventListener(factory);
    }
}
