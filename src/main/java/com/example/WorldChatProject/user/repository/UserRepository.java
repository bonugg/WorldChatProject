package com.example.WorldChatProject.user.repository;

import com.example.WorldChatProject.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{
	Optional<User> findByUserName(String userName);
	@Query(value = "SELECT U.userName FROM User U WHERE U.userNickName = :userNickNameP")
	String findUserNameByUserNickName(String userNickNameP);
	Optional<User> findByUserNickName(String userNickName);
}
