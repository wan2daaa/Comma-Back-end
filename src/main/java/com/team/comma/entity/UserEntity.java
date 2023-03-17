package com.team.comma.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserEntity implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long userKey;
	
	@Column(length = 50 , nullable = false)
	private String email;
	
	@Column(length = 50 , nullable = false)
	private String password;
	
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
	
	@ElementCollection(fetch = FetchType.EAGER)
	@Builder.Default
    private List<String> roles = new LinkedList<String>();
	
	// 연관관계 편의 메소드
	public void addUserArtist(UserArtist userArtist) {
		getArtistName().add(userArtist);
		userArtist.setUserInfo(this);
	}
	
	public void addGenreName(UserGenre userGenre) {
		getGenreName().add(userGenre);
		userGenre.setUserInfo(this);
	}

	// JWT Security
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		
		for(String role : roles){
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
