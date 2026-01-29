package com.surest.member_management.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.surest.member_management.dto.LoginRequestDto;
import com.surest.member_management.dto.LoginResponseDto;
import com.surest.member_management.entity.Member;
import com.surest.member_management.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class MemberControllerIntegrationTestWithTestcontainers {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("member_test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Member testMember;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        memberRepository.deleteAll();
        testMember = createTestMember("John", "Doe", "john.doe@example.com");
        authToken = generateAuthToken();
    }

    private Member createTestMember(String firstName, String lastName, String email) {
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
        member.setDateOfBirth(LocalDate.of(1990, 5, 15));
        member.setCreatedAt(LocalDateTime.now());
        member.setUpdatedAt(LocalDateTime.now());
        return member;
    }

    private String generateAuthToken() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setUsername("admin");
        loginRequest.setPassword("admin123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        LoginResponseDto loginResponse = objectMapper.readValue(responseBody, LoginResponseDto.class);
        return loginResponse.getToken();
    }

    @Test
    void createMember_shouldSaveAndReturnMember() throws Exception {
        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assert memberRepository.count() == 1;
    }

    @Test
    void getAllMembers_shouldReturnAllMembers() throws Exception {
        Member member1 = createTestMember("Alice", "Smith", "alice@example.com");
        Member member2 = createTestMember("Bob", "Johnson", "bob@example.com");

        memberRepository.save(member1);
        memberRepository.save(member2);

        mockMvc.perform(get("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", anyOf(is("Alice"), is("Bob"))))
                .andExpect(jsonPath("$[1].firstName", anyOf(is("Alice"), is("Bob"))));
    }

    @Test
    void getMemberById_shouldReturnMemberWhenFound() throws Exception {
        Member savedMember = memberRepository.save(testMember);

        mockMvc.perform(get("/api/v1/members/{id}", savedMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMember.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    void getMemberById_shouldReturnNotFoundWhenMemberDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/members/{id}", UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMembers_shouldReturnPagedMembers() throws Exception {
        for (int i = 0; i < 15; i++) {
            Member member = createTestMember("Member" + i, "Test", "member" + i + "@example.com");
            memberRepository.save(member);
        }

        mockMvc.perform(get("/api/v1/members/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    void getMembers_shouldReturnSecondPageWhenPageNumberIncremented() throws Exception {
        for (int i = 0; i < 15; i++) {
            Member member = createTestMember("Member" + i, "Test", "member" + i + "@example.com");
            memberRepository.save(member);
        }

        mockMvc.perform(get("/api/v1/members/paged")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "createdAt")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    void updateMember_shouldUpdateAndReturnMember() throws Exception {
        Member savedMember = memberRepository.save(testMember);

        Member updatedMember = createTestMember("Jane", "Smith", "jane.smith@example.com");

        mockMvc.perform(put("/api/v1/members/{id}", savedMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMember.getId().toString()))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        Member retrievedMember = memberRepository.findById(savedMember.getId()).orElseThrow();
        assert retrievedMember.getFirstName().equals("Jane");
    }

    @Test
    void deleteMember_shouldRemoveMemberFromDatabase() throws Exception {
        Member savedMember = memberRepository.save(testMember);
        assert memberRepository.count() == 1;

        mockMvc.perform(delete("/api/v1/members/{id}", savedMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        assert memberRepository.count() == 0;
        assert memberRepository.findById(savedMember.getId()).isEmpty();
    }

    @Test
    void completeMemberFlow_shouldHandleCreateReadUpdateDelete() throws Exception {
        // Create
        MvcResult createResult = mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(testMember)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = createResult.getResponse().getContentAsString();
        Member createdMember = objectMapper.readValue(responseBody, Member.class);
        UUID memberId = createdMember.getId();

        // Read
        mockMvc.perform(get("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));

        // Update
        Member updatedMember = createTestMember("Johnny", "Doe", "johnny@example.com");

        mockMvc.perform(put("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken)
                        .content(objectMapper.writeValueAsString(updatedMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Johnny"));

        // Delete
        mockMvc.perform(delete("/api/v1/members/{id}", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());

        assert memberRepository.findById(memberId).isEmpty();
    }

    @Test
    void multipleMembersCreation_shouldPersistCorrectlyInDatabase() throws Exception {
        for (int i = 0; i < 5; i++) {
            Member member = createTestMember("User" + i, "Test" + i, "user" + i + "@example.com");

            mockMvc.perform(post("/api/v1/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + authToken)
                            .content(objectMapper.writeValueAsString(member)))
                    .andExpect(status().isOk());
        }

        assert memberRepository.count() == 5;
    }

    @Test
    void sortingByDifferentFields_shouldReturnCorrectOrder() throws Exception {
        Member member1 = createTestMember("Zoe", "Alpha", "zoe@example.com");
        member1.setDateOfBirth(LocalDate.of(1990, 1, 1));

        Member member2 = createTestMember("Alice", "Beta", "alice@example.com");
        member2.setDateOfBirth(LocalDate.of(1995, 1, 1));

        memberRepository.save(member1);
        memberRepository.save(member2);

        mockMvc.perform(get("/api/v1/members/paged")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "firstName")
                        .param("sortDirection", "ASC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Alice"))
                .andExpect(jsonPath("$.content[1].firstName").value("Zoe"));
    }
}
