package com.team.comma.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
public class UserInfo {

	@Id
	@GeneratedValue
	private Long keyName;
	
	@OneToOne(mappedBy = "userInfo")
	private UserEntity userEntity;
	
	@Column(length = 10 , nullable = false)
	private String name;
	
	@Column(length = 10 , nullable = false)
	private String sex;
	
	@Column(length = 5 , nullable = false)
	private String age;
	
	@Column(length = 10 , nullable = false)
	private LocalDateTime recommandTime;
	// recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0))
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime joinTime;
	
	@Column(nullable = false)
	private int isLeave;
	
	@Column(nullable = true)
	private LocalDateTime leaveTime;
	
}
