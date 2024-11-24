package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private AccountResponse account;
    private OrderStatusResponse orderStatus;
    private String note;
    private Float oderAcreage;
    private Boolean paymentMethods;
    private Long totalPrice;
    private Float totalWeight;
    private Float distance;
    private Date deliveryTime;
    private Long fixedCost;
    private String image;
    private String orderCode;
    private Date createOderTime;
    private List<OrderDetailsResponse> orderDetails;
    private List<VoucherToOrderResponse> vouchers;
    private AddressAccountResponse addressAccount;
    private ContractResponse contractresponse;
}
