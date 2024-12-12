package com.gizmo.gizmoshop.vnpay;


import com.gizmo.gizmoshop.entity.Account;
import com.gizmo.gizmoshop.repository.AccountRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final VNPAYConfig vnPayConfig;
    private final AccountRepository accountRepository;
    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request , long idAccount) {
        String idWallet = request.getParameter("idWallet");
        String idAddress = request.getParameter("idAddress");
        String idVoucher = request.getParameter("idVoucher");
        String type = request.getParameter("type");
        String txnRef = "type=" + type +
                "|idAccount=" + idAccount +
                (idWallet != null && !idWallet.isEmpty() ? "|idWallet=" + idWallet : "") +
                (idAddress != null && !idAddress.isEmpty() ? "|idAddress=" + idAddress : "") +
                (idVoucher != null && !idVoucher.isEmpty() ? "|idVoucher=" + idVoucher : "") +
                "|txnRef=" + VNPayUtil.getRandomNumber(8);
        //tiến hành lưu txn ref vào tk hiện tại
        Optional<Account> account = accountRepository.findById(idAccount);
       if(account.isPresent()){
           account.get().setVnp_TxnRef(txnRef);
           accountRepository.save(account.get());
       }else{
        throw new UsernameNotFoundException("Tài khoản không tồn tại");
       }

        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(txnRef);

        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl).build();
    }
}
