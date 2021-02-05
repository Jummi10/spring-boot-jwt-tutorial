package com.example.tutorial.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutorial.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@EntityGraph(attributePaths = "authorities")
		// Eager 조회로 authorities 정보 같이 가져옴
	Optional<User> findOneWithAuthoritiesByUsername(String username);
}
