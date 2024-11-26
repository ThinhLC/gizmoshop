package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.OrderStatusResponse;
import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderStatusAPI {
    @Autowired
    OrderStatusService orderStatusService;
    @GetMapping("/orders/status")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ResponseWrapper<List<OrderStatusResponse>>> getAllOrderStatuses(@RequestParam(required = false) Integer type) {

        List<OrderStatusResponse> orderStatusResponses = orderStatusService.getOrderStatusesByType(type);

        ResponseWrapper<List<OrderStatusResponse>> responseWrapper = new ResponseWrapper<>(HttpStatus.OK, "Success", orderStatusResponses);
        return ResponseEntity.ok(responseWrapper);
    }
}