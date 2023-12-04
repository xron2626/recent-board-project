package com.example.demo.user.userAuthority;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthorityRepository extends JpaRepository<UserAuthority,Long> {
    UserAuthority findByUserId(Long userId);
}
