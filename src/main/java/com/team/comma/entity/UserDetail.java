package com.team.comma.entity;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserDetail {

	@Id
	@GeneratedValue
	private Long keyName;
	
	@OneToOne(mappedBy = "userDetail")
	private User user;
	
	@Column(length = 10 , nullable = false)
	private String name;
	
	@Column(length = 10 , nullable = false)
	private String sex;
	
	@Column(length = 5 , nullable = false)
	private String age;
	
	@Column(length = 10 , nullable = false)
	private LocalDateTime recommendTime;
	// recommandTime(LocalDateTime.of(2015, 12, 25, 12, 0))
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime joinTime;
	
	@Column(nullable = false)
	private int isLeave;
	
	@Column(nullable = true)
	private LocalDateTime leaveTime;

}
