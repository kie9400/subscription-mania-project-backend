package com.springboot.member.controller;

import com.springboot.category.dto.CategoryDto;
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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@Tag(name = "회원 API", description = "회원 관련 API")
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
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"이메일 전송이 실패했습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 이메일이 이미 존재합니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"해당 이메일이 이미 존재합니다.\"}")))
    })
    @PostMapping("/send-email")
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
    @PostMapping("/verify-code")
    public ResponseEntity verifyCode(@Valid @RequestBody MemberDto.VerifyCodeRequest reqeustDto) {
        memberService.verifyCode(reqeustDto);
        return ResponseEntity.status(HttpStatus.OK).body("인증 완료");
    }

    @Operation(summary = "회원 가입", description = "회원 가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 등록 완료"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"존재하지 않는 회원입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "회원가입 유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"회원가입 유효성 검증 실패\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 가입된 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"이미 가입된 회원입니다.\"}")))
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
            @ApiResponse(responseCode = "200", description = "이메일을 찾았습니다.",
                    content = @Content(
                            mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = MemberDto.FindId.class))
                    )),
            @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"회원을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @PostMapping("/findid")
    public ResponseEntity findId(@Valid @RequestBody MemberDto.FindId findIdDto){
        Member member = memberService.findMemberId(mapper.findIdDtoToMember(findIdDto));
        MemberDto.FindIdResponse response = mapper.memberToFindId(member);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @PostMapping("/findpw")
    public ResponseEntity findPw(@Valid @RequestBody MemberDto.FindPw findPwDto){
        memberService.findPw(mapper.findPwDtoToMember(findPwDto));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원 탈퇴(자신)", description = "자신(회원)이 탈퇴 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "회원 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"존재하지 않는 회원입니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 탈퇴한 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"이미 탈퇴한 회원입니다.\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @DeleteMapping
    public ResponseEntity myDeleteMember(@Valid @RequestBody MemberDto.Delete memberDeleteDto,
                                         @Parameter(hidden = true) @AuthenticationPrincipal Member authenticatedmember) {
        Member member = mapper.memberDeleteToMember(memberDeleteDto);
        memberService.myDeleteMember(member, authenticatedmember.getMemberId());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 완료"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"존재하지 않는 회원입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "비밀번호 유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"비밀번호 유효성 검증 실패\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @PatchMapping("/password")
    public ResponseEntity patchMember(@RequestBody @Valid MemberDto.PatchPassword passwordDto,
                                      @Parameter(hidden = true) @AuthenticationPrincipal Member authenticatedmember){
        memberService.updatePassword(passwordDto, authenticatedmember.getMemberId());

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "회원정보 수정", description = "회원 정보를 수정 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 완료"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"존재하지 않는 회원입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "회원 정보 수정 유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"유효성 검증 실패\"}"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity patchMember(@RequestPart("data") @Valid MemberDto.Patch memberPatchDto,
                                      @Parameter(hidden = true) @AuthenticationPrincipal Member authenticatedmember,
                                      @RequestPart(required = false) MultipartFile profileImage,
                                      @RequestParam(value = "imageDeleted", required = false) Boolean imageDeleted){
        Member member = memberService.updateMember(mapper.memberPatchToMember(memberPatchDto), authenticatedmember.getMemberId(), profileImage, imageDeleted);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

