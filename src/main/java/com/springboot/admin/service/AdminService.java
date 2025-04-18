package com.springboot.admin.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
@RequiredArgsConstructor
public class AdminService {
    private final MemberService memberService;

    //관리자 특정 회원 조회
    @Transactional(readOnly = true)
    public Member adminFindMembers(long memberId, long adminId){
        //관리자가 아니라면 예외를 던진다.
        if(!memberService.isAdmin(adminId)){
            throw new BusinessLogicException(ExceptionCode.ACCESS_DENIED);
        }
        Member findMember = memberService.findVerifiedMember(memberId);

        return findMember;
    }

    //전체 회원 목록 조회
    @Transactional(readOnly = true)
    public Page<Member> findMembers(int page, int size, long memberId, String keyword){
        return memberService.findMembers(page, size, memberId, keyword);
    }

    //회원 탈퇴
    public void deleteMember(Long memberId, Member member){
        memberService.deleteMember(memberId, member);
    }
}
