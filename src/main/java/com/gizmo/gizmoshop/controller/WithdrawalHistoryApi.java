package com.gizmo.gizmoshop.controller;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.dto.reponseDto.WithdrawalHistoryResponse;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import com.gizmo.gizmoshop.service.WithdrawalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/public/withdrawalhistory")
@RequiredArgsConstructor
@CrossOrigin("*")
public class WithdrawalHistoryApi {

    @Autowired
    private WithdrawalHistoryService withdrawalHistoryService;

    @GetMapping("/getall")
    public ResponseEntity<ResponseWrapper<List<WithdrawalHistoryResponse>>> getAllWithdrawalHistory(
            @RequestParam Long accountId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryByAccount(accountId, userPrincipal);
        ResponseWrapper<List<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Withdrawal history fetched successfully",
                withdrawalHistory
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/date-range")
    public ResponseEntity<ResponseWrapper<List<WithdrawalHistoryResponse>>> getWithdrawalHistoryByDateRange(
            @RequestParam Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<WithdrawalHistoryResponse> withdrawalHistory =
                withdrawalHistoryService.getWithdrawalHistoryByAccountAndDateRange(accountId, startDate, endDate, userPrincipal);
        ResponseWrapper<List<WithdrawalHistoryResponse>> response = new ResponseWrapper<>(
                HttpStatus.OK,
                "Withdrawal history fetched successfully",
                withdrawalHistory
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

