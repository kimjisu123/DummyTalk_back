package com.example.DummyTalk.Chat.Server.repository;

import com.example.DummyTalk.Chat.Server.Entity.ServerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServerRepository extends JpaRepository<ServerEntity, Long> {
    @Query("SELECT s FROM ServerEntity s " +
            "JOIN s.userChats uc " +
            "WHERE uc.user.userId = :userId")
    List<ServerEntity> findServersByUserId(@Param("userId") Long userId);


}
