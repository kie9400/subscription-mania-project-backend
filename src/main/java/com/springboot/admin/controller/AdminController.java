package com.springboot.admin.controller;

import com.springboot.admin.dto.AdminDto;
import com.springboot.admin.mapper.AdminMapper;
import com.springboot.admin.service.AdminService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

//이후 확장성을 고려하여 관리자 전용 컨트롤러 생성
//지금은 회원관리만 하지만 추후 카테고리, 플랫폼 등록 추가도 가능
@Tag(name = "관리자 페이지 API", description = "관리자만 접근 가능한 API")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final AdminMapper mapper;

    @GetMapping("/members/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") long memberId,
                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        Member findMember = adminService.adminFindMembers(memberId, member.getMemberId());
        AdminDto.MemberResponse response = mapper.responseDtoToMember(findMember);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @GetMapping("/members")
    public ResponseEntity getMembers(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size,
                                     @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Page<Member> memberPage = adminService.findMembers(page - 1 , size, member.getMemberId());
        List<Member> members = memberPage.getContent();

        return new ResponseEntity<> (new MultiResponseDto<>
                        (mapper.membersToMemberResponses(members),memberPage),HttpStatus.OK);
    }

    @DeleteMapping("/members/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") long memberId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        adminService.deleteMember(memberId, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
