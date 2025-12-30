package com.surest.member_management.controller;


import com.surest.member_management.entity.Member;
import com.surest.member_management.service.MemberService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 🔹 Create Member
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberService.createMember(member);
    }

    // 🔹 Get all Members
    @GetMapping
    public List<Member> getAllMembers() {
        return memberService.getAllMembers();
    }

    // 🔹 Get Member by ID
    @GetMapping("/{id}")
    public Member getMemberById(@PathVariable UUID id) {
        return memberService.getMemberById(id);
    }

    // 🔹 Update Member
    @PutMapping("/{id}")
    public Member updateMember(
            @PathVariable UUID id,
            @RequestBody Member member) {
        return memberService.updateMember(id, member);
    }

    // 🔹 Delete Member
    @DeleteMapping("/{id}")
    public void deleteMember(@PathVariable UUID id) {
        memberService.deleteMember(id);
    }
}