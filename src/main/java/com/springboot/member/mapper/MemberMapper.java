package com.springboot.member.mapper;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MySubsResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.subscription.entity.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {
    Member memberPostToMember(MemberDto.Post requestBody);
    MemberDto.MyPageResponse memberToMyPage(Member member);
    MemberDto.MyInfoResponse memberToMyInfo(Member member);
    Member findIdDtoToMember(MemberDto.FindId requestBody);
    MemberDto.FindIdResponse memberToFindId(Member member);
}
