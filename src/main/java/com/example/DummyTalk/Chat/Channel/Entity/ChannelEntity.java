package com.example.DummyTalk.Chat.Channel.Entity;

import com.example.DummyTalk.Chat.Server.Entity.ServerEntity;
import com.example.DummyTalk.Common.Entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "channel")
public class ChannelEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long channelId;

    @Column(nullable = false)
    private String ChannelName;


    /* 서버와의 연관관계 (자식) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id")
    private ServerEntity serverEntity;

    /* 채널과 채널 데이터와의 연관관계 (부모) */
    @OneToMany( mappedBy = "channelId", fetch = FetchType.LAZY)
    private List<ChatDataEntity> chatDataEntityList = new ArrayList<>();



}
