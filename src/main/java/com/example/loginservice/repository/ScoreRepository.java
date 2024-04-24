package com.example.loginservice.repository;

import com.example.loginservice.etity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score,Long> {

    boolean existsByGrantIdAndGrantedId(String userId, String grantedId);

    int countByGrantedId(String userId);

    Score findByGrantIdAndGrantedId(String myId, String userId);
}
