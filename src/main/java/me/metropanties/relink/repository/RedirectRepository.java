package me.metropanties.relink.repository;

import me.metropanties.relink.entity.Redirect;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedirectRepository extends MongoRepository<Redirect, String> {

}
