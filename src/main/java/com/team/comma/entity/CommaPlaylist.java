package com.team.comma.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

public class CommaPlaylist {
    @Id
    @GeneratedValue
    private Long commaKey;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST)
    private UserPlaylist userPlaylist;

    // 음악 정보 추후 수정
//    @Column(length = 10, nullable = false)
//    private Long musicKey;

    @Column(length = 1, nullable = false)
    private String alarmYn;

    @Column(length = 2 , nullable = false)
    private String alarmSetDay;

    @Column(length = 4 , nullable = false)
    private String alarmStartTime;

    @Column(length = 4 , nullable = false)
    private String alarmEndTime;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime registDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime modifyDate;
}
