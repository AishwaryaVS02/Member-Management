package com.surest.member_management.service.Impl;

import com.surest.member_management.entity.Member;
import com.surest.member_management.repository.MemberRepository;
import com.surest.member_management.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    // 🔹 Create member
    @Override
    @CacheEvict(value = "members", allEntries = true)
    public Member createMember(Member member) {
        log.info("Adding a new member");
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Override
    @Cacheable(value = "members")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Cacheable(value = "members", key = "#id")
    public Member getMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }

    @Override
    @Cacheable(
            value = "members",
            key = "'page_' + #page + '_' + #size + '_' + #sortBy + '_' + #sortDirection"
    )
    public Page<Member> getMembers(int page, int size, String sortBy, String sortDirection) {
        log.info("Fetching paged members data");

        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        return memberRepository.findAll(pageable);
    }

    @Override
    @CacheEvict(value = "members", allEntries = true)
    public Member updateMember(UUID id, Member updatedMember) {
        Member existingMember = getMemberById(id);

        existingMember.setFirstName(updatedMember.getFirstName());
        existingMember.setLastName(updatedMember.getLastName());
        existingMember.setDateOfBirth(updatedMember.getDateOfBirth());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setUpdatedAt(LocalDateTime.now());

        return memberRepository.save(existingMember);
    }

    @Override
    @CacheEvict(value = "members", allEntries = true)
    public void deleteMember(UUID id) {
        memberRepository.deleteById(id);
    }
}
