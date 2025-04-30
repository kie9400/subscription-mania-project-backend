package com.springboot.admin.mapper;

import com.springboot.admin.dto.AdminDto;
import com.springboot.member.entity.Member;
import com.springboot.platform.entity.Platform;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminMapper {
    @Mapping(target = "categoryName", source = "category.categoryName")
    AdminDto.PlatformResponse responseDtoToPlatform(Platform platform);
    AdminDto.MemberResponse responseDtoToMember(Member member);
    List<AdminDto.MemberResponse> membersToMemberResponses(List<Member> members);
}
