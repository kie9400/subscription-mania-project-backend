package com.springboot.member.service;

import com.springboot.auth.handler.MemberAuthenticationFailureHandler;
import com.springboot.auth.utils.AuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.file.StorageService;
import com.springboot.mail.service.MailService;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.entity.Member;
import com.springboot.member.repository.MemberRepository;
import com.springboot.redis.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;
    private final MailService mailService;
    private final RedisService redisService;
    private final StorageService storageService;

    @Value("${file.default-image}")
    private String defaultImagePath;

    //이메일 전송 메서드
    public void sendCode(MemberDto.EmailRequest emailRequest) {
        String email = emailRequest.getEmail();

        String code = createCode();
        try {
            mailService.sendEmail(email, code);
        } catch (MessagingException e) {
            throw new BusinessLogicException(ExceptionCode.SEND_MAIL_FAILED);
        }
        redisService.setCode(email, code);
    }

    //이메일 검증 메서드
    //레디스는 Key, Value의 형태로 되어있기 때문에 동일한 Key값의 Value가 저장되면 덮어씌움
    public void verifyCode(MemberDto.VerifyCodeRequest verifyCodeRequest) {
        String findCode = redisService.getCode(verifyCodeRequest.getEmail());
        String inputCode = String.valueOf(verifyCodeRequest.getCode());

        //인증코드가 일치하지 않으면 예외처리
        if (!findCode.equals(inputCode)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_CODE);
        }

        // (선택) 인증 성공했으므로 Redis에서 제거해도 됨
        redisService.deleteCode(verifyCodeRequest.getEmail());
    }

    public Member createMember(Member member){
        verifyExistsEmail(member.getEmail());
        verifyExistsPhoneNumber(member.getPhoneNumber());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createAuthorities(member.getEmail());
        member.setRoles(roles);
        member.setImage(defaultImagePath);

        return memberRepository.save(member);
    }

    public void myDeleteMember(Member member, long memberId){
        Member findMember = findVerifiedMember(memberId);

        if(!member.getEmail().equals(findMember.getEmail())){
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        if(!passwordEncoder.matches(member.getPassword(), findMember.getPassword())){
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        memberRepository.save(findMember);
    }

    //아이디를 찾기위한 메서드
    public Member findMemberEmail(Member member){
        return memberRepository.findByNameAndPhoneNumber(member.getName(), member.getPhoneNumber())
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    //이메일 중복 확인 메서드
    public void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    // 휴대폰 번호 중복 확인 메서드
    public void verifyExistsPhoneNumber(String phoneNumber){
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);

        if(member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_PHONE_NUMBER_EXISTS);
    }

    //사용자가 존재하는지 확인하는 메서드
    public Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        Member member = optionalMember.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return member;
    }

    //인증코드 생성 메서드
    private String createCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    //해당 사용자가 작성자(본인)인지 검증하는 메서드
    public void isAuthenticatedMember(long memberId, long authenticationMemberId){
        if(memberId != authenticationMemberId){
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
    }

    //이미지 등록
    public void uploadImage(Member member, MultipartFile multipartFile){
        Member findMember = findVerifiedMember(member.getMemberId());

        if (multipartFile != null && !multipartFile.isEmpty()){
            // 프로필 이미지 덮어쓰기 되도록 구현
            String pathWithoutExt = "members/" + findMember.getMemberId() + "/profile";
            String relativePath = storageService.store(multipartFile, pathWithoutExt);
            String imageUrl = "/images/" + relativePath;
            findMember.setImage(imageUrl);
        } else {
            findMember.setImage(defaultImagePath);
        }
    }
}


