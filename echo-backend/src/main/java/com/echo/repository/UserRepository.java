package com.echo.repository;

import com.echo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户数据访问层
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /** 通过用户名查找用户 */
    Optional<User> findByUsername(String username);

    /** 检查用户名是否存在 */
    boolean existsByUsername(String username);
}
