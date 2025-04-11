package com.springboot.member.repository;

import com.springboot.member.entity.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {
    Optional<Member> findByEmailWithQuerydsl(String email);
}

