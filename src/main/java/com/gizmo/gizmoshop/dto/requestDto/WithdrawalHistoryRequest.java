package com.gizmo.gizmoshop.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class WithdrawalHistoryRequest {
    private String newStatus;
    private String newNote;
}
