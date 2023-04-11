package com.team.comma.domain;

import com.team.comma.constant.RecommendType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "recommend_tb")
public class Recommend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RecommendType recommendType;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String comment;

    @ColumnDefault("0")
    private Integer playCount;

    @JoinColumn(name = "playlist_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Playlist playlist;

    @JoinColumn(name = "recommend_from")
    @ManyToOne(fetch = FetchType.LAZY)
    private User recommendFrom;

    @JoinColumn(name = "recommend_to")
    @ManyToOne(fetch = FetchType.LAZY)
    private User recommendTo;

}
