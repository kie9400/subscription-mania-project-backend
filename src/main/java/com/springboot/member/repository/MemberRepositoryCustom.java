package com.springboot.member.repository;

import com.springboot.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Page<Member> findByMemberStatus(Pageable pageable);
    Page<Member> findByNameKeywordAndMemberStatus(Pageable pageable, String keyword);
}

