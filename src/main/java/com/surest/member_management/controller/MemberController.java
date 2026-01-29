package com.surest.member_management.controller;


import com.surest.member_management.entity.Member;
import com.surest.member_management.service.Impl.MemberServiceImpl;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members")
public class MemberController {

    private final MemberServiceImpl memberServiceImpl;

    public MemberController(MemberServiceImpl memberServiceImpl) {
        this.memberServiceImpl = memberServiceImpl;
    }

    //Create Member
    @PostMapping
    public Member createMember(@RequestBody @Valid Member member) {
        return memberServiceImpl.createMember(member);
    }

    //Get all Members
    @GetMapping
    public List<Member> getAllMembers() {
        return memberServiceImpl.getAllMembers();
    }

    //Get Member by ID
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable UUID id) {
        return memberServiceImpl.getMemberById(id);
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Member>> getMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        Page<Member> members = memberServiceImpl.getMembers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(members);
    }

    //Update Member
    @PutMapping("/{id}")
    public Member updateMember(
            @PathVariable UUID id,
            @RequestBody @Valid Member member) {
        return memberServiceImpl.updateMember(id, member);
    }

    //Delete Member
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable UUID id) {
        memberServiceImpl.deleteMember(id);
    }
}