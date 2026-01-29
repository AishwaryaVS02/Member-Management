package com.surest.member_management.service;

import com.surest.member_management.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface MemberService {

    Member createMember(Member member);

    List<Member> getAllMembers();

    Member getMemberById(UUID id);

    Page<Member> getMembers(int page, int size, String sortBy, String sortDirection);

    Member updateMember(UUID id, Member updatedMember);

    void deleteMember(UUID id);
}
