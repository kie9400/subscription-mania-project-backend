package com.springboot.review.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import com.springboot.platform.entity.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private int recommendCount = 0;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus reviewStatus = ReviewStatus.REVIEW_POST;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "platform_id")
    private Platform platform;

    // 영속성 전이, 동기화
    public void setPlatform(Platform platform) {
        this.platform = platform;
        if (!platform.getReviews().contains(this)) {
            platform.setReview(this);
        }
    }

    public void setMember(Member member) {
        this.member = member;
        if (!member.getReviews().contains(this)) {
            member.setReview(this);
        }
    }

    public enum ReviewStatus {
        REVIEW_POST("리뷰 등록 상태"),
        REVIEW_DELETE("리뷰 삭제 상태");

        @Getter
        private String status;

        ReviewStatus(String status) {
            this.status = status;
        }
    }
}