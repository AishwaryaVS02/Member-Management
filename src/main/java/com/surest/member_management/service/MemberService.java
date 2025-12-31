package com.surest.member_management.service;


import com.surest.member_management.entity.Member;
import com.surest.member_management.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    // 🔹 Create member
    public Member createMember(Member member) {
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return memberRepository.save(member);
    }

    // 🔹 Get all members
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // 🔹 Get member by ID
    public Member getMemberById(UUID id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

    }

        public Page<Member> getMembers(int page, int size, String sortBy, String sortDirection) {
            Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
            Pageable pageable =  PageRequest.of(page, size, sort);
        Page<Member> memberPage = memberRepository.findAll(pageable);
        return memberPage;
    }

    // 🔹 Update member
    public Member updateMember(UUID id, Member updatedMember) {
        Member existingMember = getMemberById(id);

        existingMember.setFirstName(updatedMember.getFirstName());
        existingMember.setLastName(updatedMember.getLastName());
        existingMember.setDateOfBirth(updatedMember.getDateOfBirth());
        existingMember.setEmail(updatedMember.getEmail());
        existingMember.setUpdatedAt(LocalDateTime.now());

        return memberRepository.save(existingMember);
    }

    // 🔹 Delete member
    public void deleteMember(UUID id) {
        memberRepository.deleteById(id);
    }
}
