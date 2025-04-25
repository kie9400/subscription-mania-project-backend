package com.springboot.admin.mapper;

import com.springboot.admin.dto.AdminDto;
import com.springboot.member.entity.Member;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    AdminDto.MemberResponse responseDtoToMember(Member member);
    List<AdminDto.MemberResponse> membersToMemberResponses(List<Member> members);
}
