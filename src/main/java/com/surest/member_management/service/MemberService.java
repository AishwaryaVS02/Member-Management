package com.surest.member_management.service;


import com.surest.member_management.entity.Member;
import com.surest.member_management.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 🔹 Create member
    public Member createMember(Member member) {
        log.info("Adding a new member");
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    @Cacheable(value = "members")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Cacheable(value = "members", key = "#id")
    public Member getMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

    }
    @Cacheable(
            value = "members",
            key = "'page_' + #page + '_' + #size"
    )
    public Page<Member> getMembers(int page, int size, String sortBy, String sortDirection) {
        log.error("Fetching paged members data");
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable =  PageRequest.of(page, size, sort);
            pageable.getPageNumber();
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return memberPage;
    }

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

    @CacheEvict(value = "members", allEntries = true)
    public void deleteMember(UUID id) {
        memberRepository.deleteById(id);
    }
}
