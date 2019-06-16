package me.seon.bootjpaboard.domain;

import me.seon.bootjpaboard.domain.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository, QuerydslPredicateExecutor {

	Optional<User> findByUserId(String userId);

	User findByEmail(Email email);
}
