package com.springboot.member.controller;

import com.springboot.dto.SingleResponseDto;
import com.springboot.mail.service.MailService;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.utils.UriCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MemberController {
    private static final String MEMBER_DEFAULT_URL = "/members";
    private final MemberService memberService;
    private final MailService mailService;
    private final MemberMapper mapper;

    @Operation(summary = "이메일 인증코드 전송", description = "이메일로 인증코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "전송 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"이메일 전송이 실패했습니다.\"}")))
    })
    @PostMapping("/sendEmail")
    public ResponseEntity sendEmail(@RequestBody MemberDto.EmailRequest requestDto) {
        memberService.sendCode(requestDto);
        return ResponseEntity.status(HttpStatus.OK).body("인증 코드가 전송되었습니다.");
    }

    @Operation(summary = "이메일 인증코드 비교", description = "전송한 인증코드를 알맞게 입력했는지 검증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인증 완료"),
            @ApiResponse(responseCode = "400", description = "인증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"인증코드가 틀렸습니다.\"}")))
    })
    @PostMapping("/verifyCode")
    public ResponseEntity verifyCode(@Valid @RequestBody MemberDto.VerifyCodeRequest reqeustDto) {
        memberService.verifyCode(reqeustDto);
        return ResponseEntity.status(HttpStatus.OK).body("인증 완료");
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

    @Operation(summary = "아이디(이메일) 찾기", description = "자신의 아이디(이메일)을 찾습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일을 찾았습니다."),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @PostMapping("/findId")
    public ResponseEntity findIdGetMember(@Valid @RequestBody MemberDto.FindId findIdDto){
        Member member = memberService.findMemberEmail(mapper.findIdDtoToMember(findIdDto));
        MemberDto.FindIdResponse response = mapper.memberToFindId(member);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}

