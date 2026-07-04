package com.echo.repository;

import com.echo.entity.PersonaUpdateLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 画像更新日志数据访问层
 */
public interface PersonaUpdateLogRepository extends JpaRepository<PersonaUpdateLog, Long> {

    /** 查询用户的画像变更历史（按时间倒序） */
    List<PersonaUpdateLog> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
