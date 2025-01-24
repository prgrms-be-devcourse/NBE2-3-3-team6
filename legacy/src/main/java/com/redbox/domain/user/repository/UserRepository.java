package com.redbox.domain.user.repository;

import com.redbox.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndName(String email, String name);

    Optional<User> findByNameAndPhoneNumber(String name, String phoneNumber);

    @Query("SELECT u.name FROM User u WHERE u.id = :userId")
    Optional<String> findNameById(Long userId);
  
    // 이메일을 기반으로 사용자 정보를 조회
    Optional<User> findByEmail(String email);

    @Query("select count(u) from User u where u.status = 'ACTIVE' and u.roleType = 'USER'")
    Integer countActiveUser();
}
