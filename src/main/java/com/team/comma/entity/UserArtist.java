package com.team.comma.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class UserArtist {
	
	@Id
	@GeneratedValue
	private Long artistKey;
	
	@ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST)
	private UserInfo userInfo;
	
	@Column(length = 45 , nullable = false)
	private String artistName;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime updateTime;
	
}
