package com.echo.repository;

import com.echo.entity.ChatBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 聊天框数据访问层
 */
public interface ChatBoxRepository extends JpaRepository<ChatBox, UUID> {

    /** 查询用户的所有聊天框（按创建时间倒序） */
    List<ChatBox> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /** 查询用户的某个类型的聊天框 */
    List<ChatBox> findByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, ChatBox.ChatBoxType type);
}
