package com.example.springboot.repositories;

import com.example.springboot.domain.User;
import org.springframework.data.repository.CrudRepository;

public interface UserCrudRepository extends CrudRepository<User, Long> {

}
