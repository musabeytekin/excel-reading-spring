package com.example.excelreading;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "region_duration_conf")
public class RegionDurationConf {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fromRegion;
    private String toRegion;

    @Column(columnDefinition = "json")
    private String equipmentConf;

    private String direction;
}