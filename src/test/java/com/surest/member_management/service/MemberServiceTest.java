package com.surest.member_management.service;

import com.surest.member_management.entity.Member;
import com.surest.member_management.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private UUID memberId;

    @BeforeEach
    void setUp() {
        memberId = UUID.randomUUID();

        member = new Member();
        member.setId(memberId);
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john.doe@gmail.com");
        member.setDateOfBirth(LocalDate.of(1995, 5, 10));
    }

    // CREATE MEMBER
    @Test
    void createMember_shouldSaveMember() {
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member savedMember = memberService.createMember(member);

        assertNotNull(savedMember);
        assertNotNull(savedMember.getCreatedAt());
        assertNotNull(savedMember.getUpdatedAt());
        verify(memberRepository, times(1)).save(member);
    }

    // GET ALL MEMBERS
    @Test
    void getAllMembers_shouldReturnMemberList() {
        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<Member> members = memberService.getAllMembers();

        assertEquals(1, members.size());
        verify(memberRepository, times(1)).findAll();
    }

    // GET MEMBER BY ID (SUCCESS)
    @Test
    void getMemberById_shouldReturnMember() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        Member found = memberService.getMemberById(memberId);

        assertEquals(memberId, found.getId());
        verify(memberRepository, times(1)).findById(memberId);
    }

    //GET MEMBER BY ID (NOT FOUND)
    @Test
    void getMemberById_shouldThrowException_whenNotFound() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> memberService.getMemberById(memberId)
        );

        assertEquals("Member not found", exception.getMessage());
    }

    //GET MEMBERS WITH PAGINATION
    @Test
    void getMembers_shouldReturnPagedMembers() {
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("firstName").ascending());
        Page<Member> page = new PageImpl<>(List.of(member));

        when(memberRepository.findAll(pageable)).thenReturn(page);

        Page<Member> result =
                memberService.getMembers(0, 5, "firstName", "ASC");

        assertEquals(1, result.getTotalElements());
        verify(memberRepository, times(1)).findAll(any(PageRequest.class));
    }

    //UPDATE MEMBER
    @Test
    void updateMember_shouldUpdateAndSaveMember() {
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member updated = new Member();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setEmail("jane.smith@gmail.com");

        Member result = memberService.updateMember(memberId, updated);

        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("jane.smith@gmail.com", result.getEmail());
        assertNotNull(result.getUpdatedAt());

        verify(memberRepository).findById(memberId);
        verify(memberRepository).save(member);
    }

    //DELETE MEMBER
    @Test
    void deleteMember_shouldDeleteById() {
        doNothing().when(memberRepository).deleteById(memberId);

        memberService.deleteMember(memberId);

        verify(memberRepository, times(1)).deleteById(memberId);
    }
}
