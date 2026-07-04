package com.echo.repository;

import com.echo.entity.PersonaDimension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 画像维度数据访问层
 */
public interface PersonaDimensionRepository extends JpaRepository<PersonaDimension, UUID> {

    /** 查询用户的所有画像维度 */
    List<PersonaDimension> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    /** 查询用户的某个画像维度 */
    Optional<PersonaDimension> findByUserIdAndDimensionKey(UUID userId, String dimensionKey);

    /** 删除用户的所有画像维度 */
    void deleteByUserId(UUID userId);
}
