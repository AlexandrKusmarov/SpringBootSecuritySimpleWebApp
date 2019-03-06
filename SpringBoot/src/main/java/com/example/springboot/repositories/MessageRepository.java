package com.example.springboot.repositories;

import com.example.springboot.domain.Message;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findByTag(String text);
}


