package com.team.comma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class TestEntity {
    @Id @GeneratedValue
    @Column(nullable = false , name = "idtest")
    private String idtest;


    @Column(nullable = false , name = "content")
    private String content;
}
