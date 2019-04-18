package me.seon.bootjpaboard.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerResoritory extends JpaRepository<Answer, Long> {
}
