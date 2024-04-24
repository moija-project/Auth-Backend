package com.example.loginservice.repository;

import com.example.loginservice.etity.EnableAccount;
import com.example.loginservice.etity.RefreshToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface EnableAccountRepository extends CrudRepository<EnableAccount,String> {
    Optional<EnableAccount> findByUuid(String uuid);
}
