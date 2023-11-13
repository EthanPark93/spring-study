package com.ethan.security1.repository;

import com.ethan.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//CRUD 함수를 자동 생성
// @Repository 어느테이션이 없어도 Ioc 가능. JpaRepository를 상속했기 때문
public interface UserRepository extends JpaRepository<User, Integer> {
}
