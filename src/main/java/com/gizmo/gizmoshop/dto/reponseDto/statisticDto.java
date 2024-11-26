package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class statisticDto {
    //doanhthu
    private long amountShop;
    private long amountSupplier;

    //sanpham
    private Long id;
    private String name;
    private Long quantity;

    //auth
    private String nameAuth;

}
