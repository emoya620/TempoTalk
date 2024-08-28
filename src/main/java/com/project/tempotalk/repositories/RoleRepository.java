package com.project.tempotalk.repositories;

import com.project.tempotalk.models.ERole;
import com.project.tempotalk.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Repository for getting Role from the "roles" collection
@Repository
public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}