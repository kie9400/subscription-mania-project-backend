package com.springboot.admin.controller;

import com.springboot.admin.dto.AdminDto;
import com.springboot.admin.mapper.AdminMapper;
import com.springboot.admin.service.AdminService;
import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "특정 회원 상세 조회", description = "한 명의 회원의 상세 정보를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 회원 조회 완료",
                    content = @Content(
                            mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AdminDto.MemberResponse.class))
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "관리자가 아닙니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Forbidden\", \"message\": \"관리자만 접근이 가능합니다.\"}"))),
    })
    @GetMapping("/members/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") long memberId,
                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        Member findMember = adminService.adminFindMembers(memberId, member.getMemberId());
        AdminDto.MemberResponse response = mapper.responseDtoToMember(findMember);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @Operation(summary = "회원 목록 조회", description = "해당 서비스를 이용하는 회원 목록을 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 목록 조회 완료",
                    content = @Content(
                            mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = AdminDto.MemberResponse.class))
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "관리자가 아닙니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Forbidden\", \"message\": \"관리자만 접근이 가능합니다.\"}"))),
    })
    @GetMapping("/members")
    public ResponseEntity getMembers(@Positive @RequestParam int page,
                                     @Positive @RequestParam int size,
                                     @RequestParam(required = false) String keyword,
                                     @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Page<Member> memberPage = adminService.findMembers(page - 1 , size, member.getMemberId(), keyword);
        List<Member> members = memberPage.getContent();

        return new ResponseEntity<> (new MultiResponseDto<>
                        (mapper.membersToMemberResponses(members),memberPage),HttpStatus.OK);
    }

    @Operation(summary = "특정 회원 탈퇴", description = "한 명의 특정 회원을 탈퇴시킼ㄴ다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 회원 탈퇴 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "관리자가 아닙니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Forbidden\", \"message\": \"관리자만 접근이 가능합니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 탈퇴된 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"이미 탈퇴된 회원입니다.\"}"))),
    })
    @DeleteMapping("/members/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") long memberId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member) {
        adminService.deleteMember(memberId, member);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
