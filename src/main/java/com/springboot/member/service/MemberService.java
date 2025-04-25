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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import java.util.*;

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
        //이미 회원가입된 이메일인지 검증
        verifyExistsEmail(email);

        String code = createCode();
        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("code", code);
            String subject = "[구독매니아] 이메일 인증 코드";
            String template = "verify-code";

            mailService.sendTemplateEmail(email, template, subject, variables);
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
        verifyExistsName(member.getName());

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createAuthorities(member.getEmail());
        member.setRoles(roles);
        member.setImage(defaultImagePath);

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findMember(Long memberId){
        return findVerifiedMember(memberId);
    }

    public Member updateMember(Member member, Long memberId, MultipartFile profileImage, boolean imageDeleted){
        Member findMember = findVerifiedMember(memberId);
        validateQuitMember(findMember);

        Optional.ofNullable(member.getName())
                .ifPresent(findMember::setName);
        Optional.ofNullable(member.getGender())
                .ifPresent(findMember::setGender);
        Optional.of(member.getAge())
                .ifPresent(findMember::setAge);

        if (imageDeleted && profileImage == null) {
            findMember.setImage(defaultImagePath);
        } else {
            //이미지 업로드, null이면 변경하지않음
            uploadImage(findMember, profileImage);
        }

        return memberRepository.save(findMember);
    }

    public void updatePassword(MemberDto.PatchPassword dto, long memberId){
        Member findMember = findVerifiedMember(memberId);
        //기존 비밀번호랑 해당 회원의 DB 비밀번호가 같은지 비교
        if(!passwordEncoder.matches(dto.getCurrentPassword(), findMember.getPassword())){
            throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_MATCHED);
        }

        if(dto.getCurrentPassword().equals(dto.getNewPassword())){
            throw new BusinessLogicException(ExceptionCode.PASSWORD_SAME_AS_OLD);
        }

        //새로운 비밀번호로 수정
        String encoded = passwordEncoder.encode(dto.getNewPassword());
        findMember.setPassword(encoded);
        memberRepository.save(findMember);
    }

    @Transactional(readOnly = true)
    public Page<Member> findMembers(int page, int size, long memberId, String keyword){
        //관리자 인지 확인(관리자만 회원 전체를 조회할 수 있어야한다.)
        if(!isAdmin(memberId)){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_ADMIN);
        }
        Pageable pageable = PageRequest.of(page, size);

        if(keyword == null){
            return memberRepository.findByMemberStatus(pageable);
        }

        return memberRepository.findByNameKeywordAndMemberStatus(pageable, keyword);
    }

    //자기 자신을 탈퇴시키는 메서드(본인)
    public void myDeleteMember(Member member, Long memberId){
        Member findMember = findVerifiedMember(memberId);
        validateQuitMember(findMember);

        if(!member.getEmail().equals(findMember.getEmail())){
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        if(!passwordEncoder.matches(member.getPassword(), findMember.getPassword())){
            throw new BusinessLogicException(ExceptionCode.INVALID_CREDENTIALS);
        }

        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        memberRepository.save(findMember);
    }

    //관리자의 회원탈퇴
    public void deleteMember(long memberId, Member admin){
        if(!isAdmin(admin.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }

        //탈퇴할 멤버 찾기
        Member findMember = findVerifiedMember(memberId);

        validateQuitMember(findMember);

        findMember.setMemberStatus(Member.MemberStatus.MEMBER_QUIT);
        memberRepository.save(findMember);
    }

    //탈퇴된 회원인지 검증하는 메서드 (탈퇴된 회원이면 예외처리)
    public void validateQuitMember(Member member){
        if(member.getMemberStatus().equals(Member.MemberStatus.MEMBER_QUIT)){
            throw new BusinessLogicException(ExceptionCode.MEMBER_DELETE);
        }
    }

    //아이디를 찾기위한 메서드
    @Transactional(readOnly = true)
    public Member findMemberEmail(Member member){
        Optional<Member> optionalMember = memberRepository.findByPhoneNumber(member.getPhoneNumber());
        Member findMember = optionalMember.orElseThrow(()->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //해당 회원이 탈퇴된 회원이면 예외처리
        validateQuitMember(findMember);
        return findMember;
    }

    //이메일 중복 확인 메서드
    @Transactional(readOnly = true)
    public void verifyExistsEmail(String email){
        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    // 닉네임 중복 확인 메서드
    @Transactional(readOnly = true)
    public void verifyExistsName(String name){
        Optional<Member> member = memberRepository.findByName(name);

        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
    }

    // 휴대폰 번호 중복 확인 메서드
    @Transactional(readOnly = true)
    public void verifyExistsPhoneNumber(String phoneNumber){
        Optional<Member> member = memberRepository.findByPhoneNumber(phoneNumber);

        if(member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_PHONE_NUMBER_EXISTS);
    }

    //사용자가 존재하는지 확인하는 메서드
    @Transactional(readOnly = true)
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
            String pathWithoutExt = "/members/" + findMember.getMemberId() + "/profile";
            //String relativePath = storageService.store(multipartFile, pathWithoutExt);
            //String imageUrl = "/images/" + relativePath;

            //실제 업로드는 S3에 하지만 DB에선 상대경로만 저장하여 DB수정없이 대응 가능하도록 한다.
            String imageUrl = storageService.store(multipartFile, pathWithoutExt);
            findMember.setImage(pathWithoutExt);
        }
    }

    //관리자 여부 확인 메서드
    public boolean isAdmin(long memberId){
        Member member = findVerifiedMember(memberId);
        return member.getRoles().contains("ADMIN");
    }
}


