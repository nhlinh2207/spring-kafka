package com.example.retryconsumer.repository;

import com.example.retryconsumer.entity.FailedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFailMessageRepo extends JpaRepository<FailedMessage, Long> {
}
