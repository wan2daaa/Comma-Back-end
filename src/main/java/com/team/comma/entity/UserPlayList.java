package com.team.comma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
public class UserPlayList {

    @Id
    @GeneratedValue
    private Long playKey;

//    @Column(length = 10, nullable = false)
//    private Long musicKey;

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
