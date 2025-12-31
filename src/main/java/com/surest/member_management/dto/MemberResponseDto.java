package com.surest.member_management.dto;


import lombok.*;
import java.time.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {

    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
