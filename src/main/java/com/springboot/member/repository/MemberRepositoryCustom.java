package com.springboot.member.repository;

import com.springboot.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByEmailWithQuerydsl(String email);
    Page<Member> findByMemberStatus(Member.MemberStatus status, Pageable pageable);
}

