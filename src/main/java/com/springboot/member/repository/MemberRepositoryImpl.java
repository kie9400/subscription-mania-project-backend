package com.springboot.member.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.QMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

//QueryDSL 구현 클래스
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QMember member = QMember.member;
    //Optional<Member> findByEmail(String email) ->  queryDsl
    @Override
    public Optional<Member> findByEmailWithQuerydsl(String email) {
        Member result = queryFactory
                .selectFrom(member)
                .where(member.email.eq(email))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<Member> findByMemberStatus(Pageable pageable) {
        List<Member> members = queryFactory
                .selectFrom(member)
                .where(
                        //멤버가 활동상태인것만(삭제, 휴면 제외)
                        member.memberStatus.eq(Member.MemberStatus.MEMBER_ACTIVE)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdAt.desc())
                .fetch();

        //회원 총 인원 수
        Long total = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        member.memberStatus.eq(Member.MemberStatus.MEMBER_ACTIVE)
                )
                .fetchOne();
        return new PageImpl<>(members, pageable, total != null ? total : 0);
    }
}
