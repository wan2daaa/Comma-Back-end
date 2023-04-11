package com.team.comma.domain;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.arch.Processor.Arch;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tb")
public class User extends BaseEntity implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100, nullable = false)
	private String email;
	@Column(length = 10 , nullable = false)
	private String name;

	@Column(length = 10)
	private String sex;

	@Column(length = 5)
	private Integer age;

	@Column(length = 50)
	private String password;

	@Column(length = 10 , nullable = false)
	private LocalTime recommendTime;

	private String nickname;

	private boolean soundFlag;

	private boolean vibrateFlag;

	// OAuth 로그인 유저인지 , 기본 로그인 유저인지 확인
	@Enumerated(EnumType.STRING)
	private UserType type;

	@Enumerated(EnumType.STRING)
	private UserRole role;

	@OneToMany(mappedBy = "user")
	private List<Archive> archiveList;

	@OneToMany(mappedBy = "user")
	private List<FavoriteArtist> artistNames;

	@OneToMany(mappedBy = "genreName")
	private List<FavoriteGenre> genreNames;

	//연관관계 편의 메소드
	public void addArchiveList(Archive archive) {
		getArchiveList().add(archive);
			archive.setUser(this);
	}

	public void addFavoriteArtists(FavoriteArtist favoriteArtist) {
		getArtistNames().add(favoriteArtist);
			favoriteArtist.setUser(this);
	}

	public void addFavoriteGenres(FavoriteGenre favoriteGenre) {
		getGenreNames().add(favoriteGenre);
		favoriteGenre.setUser(this);
	}

	// JWT Security
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(role.getKey()));
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
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
