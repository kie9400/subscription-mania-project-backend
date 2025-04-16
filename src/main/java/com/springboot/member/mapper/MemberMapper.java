package com.springboot.member.mapper;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MySubsResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.review.entity.Review;
import com.springboot.subscription.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post requestBody);
    Member memberPatchToMember(MemberDto.Patch requestBody);
    MemberDto.MyPageResponse memberToMyPage(Member member);
    MemberDto.MyInfoResponse memberToMyInfo(Member member);
    Member findIdDtoToMember(MemberDto.FindId requestBody);
    MemberDto.FindIdResponse memberToFindId(Member member);
    Member memberDeleteToMember(MemberDto.Delete requestBody);
    List<MemberDto.ReviewsResponse> reviewToMyReviews(List<Review>reviews);

    @Mapping(target = "reviewId", source = "reviewId")
    @Mapping(target = "platformImage", source = "platform.platformImage")
    @Mapping(target = "platformName", source = "platform.platformName")
    @Mapping(target = "content", source = "content")
    @Mapping(target = "rating", source = "rating")
    MemberDto.ReviewsResponse reviewToMyReview(Review review);

    default MySubsResponseDto memberToMySubsResponse(List<Subscription> subscriptions){
        //groupingBy()로 카테고리 이름 기준으로 묶음
        //groupingBy()는 자바 Stram API에서 지원하는 메서드, 컬렉션 요소들을 특정 기준(key)로 묶어 Map으로 반환해주는 함수
        //여기선 카테고리 이름이 key값이고, 구독리스트가 Valie값
        Map<String, List<MySubsResponseDto.SubscriptionInfo>> grouped = subscriptions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getSubsPlan().getPlatform().getCategory().getCategoryName(),
                        Collectors.mapping(s -> MySubsResponseDto.SubscriptionInfo.builder()
                                        .subscriptionId(s.getSubscriptionId())
                                        .platformImage(s.getSubsPlan().getPlatform().getPlatformImage())
                                        .platformName(s.getSubsPlan().getPlatform().getPlatformName())
                                        .planName(s.getSubsPlan().getPlanName())
                                        .price(s.getSubsPlan().getPrice())
                                        .build(),
                                Collectors.toList())
                ));

        // 카테고리별 총 요금과 이미지 포함된 Group List 생성
        List<MySubsResponseDto.CategoryGroup> categoryGroups = new ArrayList<>();

        // 위에서 분류한 카테고리 별 구독리스트를 순회
        //Map.Entry는 각 ket-value 쌍을 하나의 객체처럼 다룰 수 있도록 제공하는 인터페이스
        for (Map.Entry<String, List<MySubsResponseDto.SubscriptionInfo>> entry : grouped.entrySet()) {
            // 각 카테고리의 이름(key)과 구독리스트(value)를 꺼내온다.
            String categoryName = entry.getKey();
            List<MySubsResponseDto.SubscriptionInfo> subsList = entry.getValue();

            // 내가 구독한 플랫폼의 카테고리 이미지를 가져온다.
            String categoryImage = subscriptions.stream()
                    .filter(subs -> subs.getSubsPlan().getPlatform().getCategory().getCategoryName().equals(categoryName))
                    .findFirst()
                    .map(subs -> subs.getSubsPlan().getPlatform().getCategory().getCategoryImage())
                    .orElse(null);

            // 카테고리별 총 요금 계산
            int categoryTotalPrice = subsList.stream()
                    .mapToInt(MySubsResponseDto.SubscriptionInfo::getPrice)
                    .sum();

            // 하나의 카테고리 그룹 ResponseDto 생성
            categoryGroups.add(
                    MySubsResponseDto.CategoryGroup.builder()
                            .categoryName(categoryName)
                            .categoryImage(categoryImage)
                            .categoryTotalPrice(categoryTotalPrice)
                            .subscriptions(subsList)
                            .build()
            );
        }

        // 내 구독 내역 총합 요금 계산
        int total = categoryGroups.stream()
                .mapToInt(MySubsResponseDto.CategoryGroup::getCategoryTotalPrice)
                .sum();

        // MySubsResponseDto 반환하여 이 내용을 응답 body 로 보낸다.
        return MySubsResponseDto.builder()
                .totalMonthlyPrice(total)
                .categories(categoryGroups)
                .build();
    }
}
