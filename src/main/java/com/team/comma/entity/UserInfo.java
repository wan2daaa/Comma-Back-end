package com.team.comma.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class UserInfo {

	@Id
	@GeneratedValue
	private Long userKey;
	
	@Column(length = 50 , nullable = false)
	private String email;
	
	@Column(length = 10 , nullable = false)
	private String name;
	
	@Column(length = 10 , nullable = false)
	private String sex;
	
	@Column(length = 5 , nullable = false)
	private String age;
	
	@Column(length = 10 , nullable = false)
	private LocalDateTime recommandTime;
	
	@CreationTimestamp
	@Column(nullable = false)
	private LocalDateTime joinTime;
	
	@Column(nullable = false)
	private int isLeave;
	
	@Column(nullable = true)
	private LocalDateTime leaveTime;
	
	@OneToMany(mappedBy = "userInfo" , cascade = CascadeType.PERSIST , orphanRemoval = true)
	private List<UserArtist> artistName;
	
	@OneToMany(mappedBy = "genreName" , cascade = CascadeType.PERSIST , orphanRemoval = true)
	private List<UserGenre> genreName;
	
	// 연관관계 편의 메소드
	public void addUserArtist(UserArtist userArtist) {
		getArtistName().add(userArtist);
		userArtist.setUserInfo(this);
	}
	
	public void addGenreName(UserGenre userGenre) {
		getGenreName().add(userGenre);
		userGenre.setUserInfo(this);
	}
	
}
