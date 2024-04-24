package com.example.loginservice.repository;

import com.example.loginservice.etity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.nickname = :newNickname WHERE u.username= :userId")
    void updateNickname(@Param(value = "newNickname") String newNickname, @Param(value = "userId")String userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.profile = :newProfile WHERE u.username= :userId")
    void updateProfile(@Param(value = "newProfile") String newProfile, @Param(value = "userId")String userId);

    boolean existsByNickname(String newNickname);

    Optional<User> findByEmailAndIsEnabledFalse(String email);

    Optional<User> findByUsernameAndIsEnabledTrue(String username);
    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.isEnabled = :state WHERE u.email= :email")
    void updateEnable(@Param(value = "email")String Email,@Param(value = "state") boolean state);

    boolean existsByEmail(String email);

    boolean existsByUsername(String userid);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.reliabilityUser = :score WHERE u.username = :userId")
    void updateReliabilityUser(@Param(value = "userId")String userId, @Param(value = "score")float score);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.password = :newPassword WHERE u.username = :userId")
    void updatePassword(String userId, String newPassword);
}
