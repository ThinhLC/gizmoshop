package com.gizmo.gizmoshop.vnpay;

import com.gizmo.gizmoshop.dto.reponseDto.ResponseWrapper;
import com.gizmo.gizmoshop.sercurity.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/public/payment")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;
    @Value("${customer.url}")
    private String customerUrl;
    @GetMapping("/vn-pay")
    public ResponseWrapper<PaymentDTO.VNPayResponse> pay(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            HttpServletRequest request) {
        return new ResponseWrapper<>(HttpStatus.OK, "Success", paymentService.createVnPayPayment(request ,userPrincipal.getUserId()));
    }
    @GetMapping("/vn-pay-callback")
    public void  payCallbackHandler(HttpServletRequest request , HttpServletResponse response) {
        String status = request.getParameter("vnp_ResponseCode");
        String txnRef = request.getParameter("vnp_TxnRef");
        String amountStr = request.getParameter("vnp_Amount");
//        type_idaccount_idwallet_idaddress_
        //order_9_6_37_836543
        String redirectUrl;
        if (status.equals("00")) {
            //lấy type ra kiểm thử là loại nào ,
            // nếu là đơn hàng của ngời bth thì chuển qua service của phúc
            // nếu là nộp tiền cho ncc thì tìm với id rồi + giá tiê vào
            redirectUrl = customerUrl +"/payment/payment-success";
        } else {
            redirectUrl = customerUrl +"/payment/payment-failed";
        }
        redirectUrl += "?status=" + status + "&txnRef=" + txnRef + "&amount=" + amountStr;
        try {
            response.sendRedirect(redirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
