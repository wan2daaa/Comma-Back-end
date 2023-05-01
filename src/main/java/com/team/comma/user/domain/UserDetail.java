package com.team.comma.user.domain;

import com.team.comma.util.converter.BooleanConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "user_detail_tb")
public class UserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10)
    private String sex;

    @Column
    private int age;

    @Column(length = 10)
    private LocalTime recommendTime;

    @Column(length = 10)
    private String nickname;

    @Column(length = 50)
    private String profileImageUrl;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean popupAlertFlag = true;
    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean favoritePublicFlag = true;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean calenderPublicFlag = true;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean allPublicFlag = true;
}



