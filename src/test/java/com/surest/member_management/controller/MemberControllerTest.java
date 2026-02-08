package com.surest.member_management.controller;

import com.surest.member_management.entity.Member;
import com.surest.member_management.service.Impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberControllerTest {

    @Mock
    private MemberServiceImpl memberServiceImpl;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createMember_callsServiceAndReturnsMember() {
        // Arrange
        Member member = new Member();
        member.setId(UUID.randomUUID());
        when(memberServiceImpl.createMember(member)).thenReturn(member);

        // Act
        Member result = memberController.createMember(member);

        // Assert
        assertNotNull(result);
        assertEquals(member.getId(), result.getId());
        verify(memberServiceImpl).createMember(member);
    }

    @Test
    void getAllMembers_returnsListOfMembers() {
        // Arrange
        Member member1 = new Member();
        Member member2 = new Member();
        List<Member> members = Arrays.asList(member1, member2);
        when(memberServiceImpl.getAllMembers()).thenReturn(members);

        // Act
        List<Member> result = memberController.getAllMembers();

        // Assert
        assertEquals(2, result.size());
        verify(memberServiceImpl).getAllMembers();
    }

    @Test
    void getMemberById_returnsMember() {
        // Arrange
        UUID id = UUID.randomUUID();
        Member member = new Member();
        member.setId(id);
        when(memberServiceImpl.getMemberById(id)).thenReturn(member);

        // Act
        Member result = memberController.getMemberById(id);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(memberServiceImpl).getMemberById(id);
    }

    @Test
    void getMembers_returnsPagedResponse() {
        // Arrange
        Member member = new Member();
        Page<Member> page = new PageImpl<>(List.of(member));
        when(memberServiceImpl.getMembers(0, 10, "createdAt", "DESC")).thenReturn(page);

        // Act
        ResponseEntity<Page<Member>> response = memberController.getMembers(0, 10, "createdAt", "DESC");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getBody().getContent().size());
        verify(memberServiceImpl).getMembers(0, 10, "createdAt", "DESC");
    }

    @Test
    void updateMember_callsServiceAndReturnsUpdatedMember() {
        // Arrange
        UUID id = UUID.randomUUID();
        Member member = new Member();
        member.setId(id);
        when(memberServiceImpl.updateMember(id, member)).thenReturn(member);

        // Act
        Member result = memberController.updateMember(id, member);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(memberServiceImpl).updateMember(id, member);
    }

    @Test
    void deleteMember_callsService() {
        // Arrange
        UUID id = UUID.randomUUID();
        doNothing().when(memberServiceImpl).deleteMember(id);

        // Act
        memberController.deleteMember(id);

        // Assert
        verify(memberServiceImpl).deleteMember(id);
    }
}
