package com.springboot.category.entity;

import com.springboot.platform.entity.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private String categoryImage;

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<Platform> platforms = new ArrayList<>();

    public void setPlatform(Platform platform){
        platforms.add(platform);
        if (platform.getCategory() != this){
            platform.setCategory(this);
        }
    }
}
