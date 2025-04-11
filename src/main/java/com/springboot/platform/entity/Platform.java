package com.springboot.platform.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Platform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long platformId;

    @Column(nullable = false)
    private String platformName;

    @Column(nullable = false)
    private String Image;

    @Column(name = "service_at", updatable = false, nullable = false)
    private LocalDateTime serviceAt;
}
