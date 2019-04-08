package com.example.springboot.repositories;

import com.example.springboot.domain.Message;
import com.example.springboot.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Page<Message> findByTag(String tag, Pageable pageable);
    Page<Message> findAll(Pageable pageable);
    Page<Message> findByAuthor(User user, Pageable pageable);
}


