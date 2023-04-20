package com.team.comma.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_detail_tb")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String sex;

    @Column(length = 5)
    private Integer age;

    @Column(length = 10)
    private LocalTime recommendTime;

    @Column(length = 10)
    private String nickname;

    @Column(length = 50)
    private String profileImageUrl;

    private Boolean popupAlertFlag;
    private Boolean favoritePublicFlag;

    private Boolean calenderPublicFlag;

    private Boolean allPublicFlag;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
