package com.springboot.member.entity;

import com.springboot.audit.BaseEntity;

import com.springboot.review.entity.Review;
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
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String image;

    @Column(nullable = false, updatable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 13, unique = true)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int age;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private MemberStatus memberStatus = MemberStatus.MEMBER_ACTIVE;

    @Enumerated(value = EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Gender gender;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Review> reviews = new ArrayList<>();

    public void setReview(Review review){
        reviews.add(review);
        if (review.getMember() != this){
            review.setMember(this);
        }
    }

    public enum MemberStatus {
        MEMBER_ACTIVE("활동 상태"),
        MEMBER_SLEEP("휴면 상태"),
        MEMBER_QUIT("탈퇴 상태");

        @Getter
        private String status;

        MemberStatus(String status) {
            this.status = status;
        }
    }

    public enum Gender {
        MALE("남자"),
        FEMALE("여자");

        @Getter
        private String gender;

        Gender(String gender) {
            this.gender = gender;
        }
    }
}
