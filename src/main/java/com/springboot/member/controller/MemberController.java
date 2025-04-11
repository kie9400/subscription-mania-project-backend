package com.springboot.member.controller;

import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.utils.UriCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/members")
@Validated
public class MemberController {
    private static final String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final MemberMapper mapper;


    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 등록 완료"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 이메일입니다."),
            @ApiResponse(responseCode = "409", description = "이미 가입한 회원입니다.")
    })
    @PostMapping
    public ResponseEntity postMember(@RequestBody @Valid MemberDto.Post memberPostDto) {
        // Mapper를 통해 받은 Dto 데이터 Member로 변환
        Member member = mapper.memberPostToMember(memberPostDto);
        Member createdMember = memberService.createMember(member);
        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, createdMember.getMemberId());

        return ResponseEntity.created(location).build();
    }
}

