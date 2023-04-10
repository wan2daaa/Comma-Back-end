package com.team.comma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table
@NoArgsConstructor
@AllArgsConstructor
public class MusicEntity {
    @Id
    @GeneratedValue
    private Long musicKey;

    @Column(length = 100, nullable = false)
    private String musicTitle;

    @Column(length = 100, nullable = false)
    private String artistName;

    @Column(length = 100, nullable = false)
    private String albumName;

    @Column(length = 100, nullable = false)
    private String albumImage;

    @Column(length = 10, nullable = false)
    private String playTime;

    @Column(length = 1, nullable = false)
    private String alarmYn;

    @Column(length = 1, nullable = false)
    private String likeYn;

    @Column(length = 4)
    private Long playCount;

    @ManyToOne(fetch = FetchType.LAZY , cascade = CascadeType.PERSIST)
    private UserPlaylist userPlaylist;

    @PrePersist
    public void prePersist(){
        this.alarmYn = this.alarmYn == null ? "Y" : this.alarmYn;
        this.likeYn = this.likeYn == null ? "N" : this.likeYn;
    }

}
