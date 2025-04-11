package com.springboot.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.QMember;

import java.util.Optional;

//QueryDSL 구현 클래스
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    //Optional<Member> findByEmail(String email) ->  queryDsl
    @Override
    public Optional<Member> findByEmailWithQuerydsl(String email) {
        QMember member = QMember.member;

        Member result = queryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
