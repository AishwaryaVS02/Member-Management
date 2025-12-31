package com.surest.member_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_management.config.JwtUtil;
import com.surest.member_management.entity.Member;
import com.surest.member_management.service.CustomUserDetailsService;
import com.surest.member_management.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;

    private Member createMember() {
        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setFirstName("John");
        member.setLastName("Doe");
        member.setEmail("john.doe@gmail.com");
        member.setDateOfBirth(LocalDate.of(1995, 5, 10));
        return member;
    }

    //CREATE MEMBER
    @Test
    void createMember_shouldReturnCreatedMember() throws Exception {
        Member member = createMember();

        when(memberService.createMember(any(Member.class)))
                .thenReturn(member);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(memberService).createMember(any(Member.class));
    }

    //GET ALL MEMBERS
    @Test
    void getAllMembers_shouldReturnList() throws Exception {
        when(memberService.getAllMembers())
                .thenReturn(List.of(createMember()));

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("john.doe@gmail.com"));

        verify(memberService).getAllMembers();
    }

    //GET MEMBER BY ID
    @Test
    void getMemberById_shouldReturnMember() throws Exception {
        Member member = createMember();

        when(memberService.getMemberById(member.getId()))
                .thenReturn(member);

        mockMvc.perform(get("/members/{id}", member.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        verify(memberService).getMemberById(member.getId());
    }

    //GET MEMBERS WITH PAGINATION
    @Test
    void getMembersPaged_shouldReturnPage() throws Exception {
        Page<Member> page = new PageImpl<>(List.of(createMember()));

        when(memberService.getMembers(0, 10, "createdAt", "DESC"))
                .thenReturn(page);

        mockMvc.perform(get("/members/paged")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("John"));

        verify(memberService)
                .getMembers(0, 10, "createdAt", "DESC");
    }

    //UPDATE MEMBER
    @Test
    void updateMember_shouldReturnUpdatedMember() throws Exception {
        Member member = createMember();

        when(memberService.updateMember(eq(member.getId()), any(Member.class)))
                .thenReturn(member);

        mockMvc.perform(put("/members/{id}", member.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(member)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(memberService)
                .updateMember(eq(member.getId()), any(Member.class));
    }

    //DELETE MEMBER
    @Test
    void deleteMember_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(memberService).deleteMember(id);

        mockMvc.perform(delete("/members/{id}", id))
                .andExpect(status().isOk());

        verify(memberService).deleteMember(id);
    }
}
