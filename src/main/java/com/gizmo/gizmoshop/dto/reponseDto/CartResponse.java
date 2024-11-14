package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private AccountResponse accountId;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private Long totalPrice;
    private List<CartItemResponse> items;

    public CartResponse(Long id, LocalDateTime createDate, LocalDateTime updateDate, Long totalPrice, List<CartItemResponse> items) {
        this.id = id;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    public CartResponse(AccountResponse accountId) {
        this.accountId = accountId;
    }
}
