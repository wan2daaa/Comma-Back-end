package com.team.comma.user.domain;

import com.team.comma.spotify.history.domain.History;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.util.converter.BooleanConverter;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_tb")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 50)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Builder.Default
    @Convert(converter = BooleanConverter.class)
    private Boolean delFlag = false;

    @Setter
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_detail_id")
    private UserDetail userDetail;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Builder.Default
    private List<FavoriteGenre> favoriteGenre = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Builder.Default
    private List<FavoriteArtist> favoriteArtist = new ArrayList<>();

    @OneToMany(cascade = CascadeType.PERSIST , mappedBy = "user")
    @Builder.Default
    private List<History> history = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addFavoriteGenre(String genre) {
        FavoriteGenre genreData = FavoriteGenre.builder()
            .genreName(genre)
            .user(this)
            .build();

        favoriteGenre.add(genreData);
    }

    public void addFavoriteArtist(String artist) {
        FavoriteArtist artistData = FavoriteArtist.builder()
            .artistName(artist)
            .user(this)
            .build();

        favoriteArtist.add(artistData);
    }

    public void addHistory(String history) {
        History historyEntity = History.builder()
                .searchHistory(history)
                .user(this)
                .build();

        this.history.add(historyEntity);
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
