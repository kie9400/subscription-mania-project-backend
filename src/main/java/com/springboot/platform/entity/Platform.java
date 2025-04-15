package com.springboot.platform.entity;

import com.springboot.category.entity.Category;
import com.springboot.plan.entity.SubsPlan;
import com.springboot.review.entity.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private String platformDescription;

    @Column(nullable = false)
    private double ratingAvg;

    @Column(nullable = false)
    private int reviewCount;

    @Column(nullable = false)
    private String platformImage;

    @Column(name = "service_at", updatable = false, nullable = false)
    private LocalDate serviceAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "platform", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "platform", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<SubsPlan> subsPlans = new ArrayList<>();

    public void setReview(Review review){
        reviews.add(review);
        if (review.getPlatform() != this){
            review.setPlatform(this);
        }
    }

    public void setSubsPlan(SubsPlan subsPlan){
        subsPlans.add(subsPlan);
        if (subsPlan.getPlatform() != this){
            subsPlan.setPlatform(this);
        }
    }

    public void setCategory(Category category){
        this.category = category;
        if (!category.getPlatforms().contains(this)) {
            category.setPlatform(this);
        }
    }
}
