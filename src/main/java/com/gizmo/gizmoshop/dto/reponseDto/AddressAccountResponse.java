package com.gizmo.gizmoshop.dto.reponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class AddressAccountResponse {
    private Long id;
    private String fullname;
    private String specificAddress;
    private String sdt;
    private String city;
    private String district;
    private String commune;
    private String longitude;
    private String latitude;
    private Boolean deleted;
}
