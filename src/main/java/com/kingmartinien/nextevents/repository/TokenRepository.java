package com.kingmartinien.nextevents.repository;

import com.kingmartinien.nextevents.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Query("""
             SELECT T FROM Token AS T INNER JOIN User AS U ON T.user.id = U.id\s
             WHERE U.id = :userId AND (T.expired = false OR T.revoked = false)
            \s""")
    List<Token> findAllValidTokensByUser(Long userId);

    void deleteAllByExpiredAndRevoked(boolean expired, boolean revoked);

}
