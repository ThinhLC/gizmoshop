package com.gizmo.gizmoshop.dto.reponseDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter

public class VoucherCardResponseDto {
    private Long id;
    private boolean status;
    private boolean hasRemainingDays; // "còn ngày sử dụng"
    private boolean hasRemainingUses;  // "còn số lượng dùng"
}