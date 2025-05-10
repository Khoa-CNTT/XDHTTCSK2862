package com.example.myevent_be.controller;


import com.example.myevent_be.configuration.VnpayConfig;
import com.example.myevent_be.dto.PaymentResDTO;
import com.nimbusds.jose.shaded.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;


@RestController
@RequestMapping("/api/payment")
public class PaymentController  {
    @GetMapping("/create-payment")
    public ResponseEntity<?> createPayment() throws UnsupportedEncodingException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        //       long amount = Integer.parseInt(req.getParameter("amount"))*100;
        long amount =1000*100;
        String bankCode ="NCB";
        String expectedSecret="0CS6330TJNJM2TEC539KYR2JTTBS0PWW";
        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        //     String vnp_IpAddr = VnpayConfig.getIpAddress(req);

        String   vnp_IpAddr = "127.0.0.1";
        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", bankCode);

        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

//        List fieldNames = new ArrayList(vnp_Params.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder hashData = new StringBuilder();
//        StringBuilder query = new StringBuilder();
//        Iterator itr = fieldNames.iterator();
//        for (Map.Entry<String, String> entry : vnp_Params.entrySet()) {
//            String fieldName = (String) itr.next();
//            String fieldValue = (String) vnp_Params.get(fieldName);
//            if ((fieldValue != null) && (fieldValue.length() > 0)) {
//                // Build hash data (KHÔNG encode fieldValue)
//                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));;
//                if (hashData.charAt(hashData.length() - 1) == '+') {
//                    hashData.setCharAt(hashData.length() - 1, ' ');
//                }
//                hashData.append('&');
//            }
//        }
//        if (hashData.length() > 0) {
//            hashData.setLength(hashData.length() - 1);
//        }
//        String hashDataStr = hashData.toString();

//
////        String queryUrl = query.toString();
//        String vnp_SecureHash = VnpayConfig.hmacSHA512(expectedSecret, hashDataStr);
//        //queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
//        String paymentUrl = VnpayConfig.vnp_Url + "?" + hashDataStr+ "&vnp_SecureHash=" + vnp_SecureHash;;
        vnp_Params.remove("vnp_SecureHash"); // Bắt buộc loại bỏ nếu có

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                String encodedName = URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString());
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString());

                hashData.append(fieldName).append("=").append(encodedValue);
                query.append(encodedName).append("=").append(encodedValue);

                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        String paymentUrl = VnpayConfig.vnp_Url + "?" + query.toString();
        // Thêm log debug
        System.out.println("hashData: " + hashData.toString());
        System.out.println("vnp_SecureHash: " + vnp_SecureHash);
        System.out.println("paymentUrl: " + paymentUrl);

        PaymentResDTO paymentResDTO = new PaymentResDTO();
        paymentResDTO.setStatus("Ok");
        paymentResDTO.setMessage("successfully");
        paymentResDTO.setURL(paymentUrl);
//        com.google.gson.JsonObject job = new JsonObject();
//        job.addProperty("code", "00");
//        job.addProperty("message", "success");
//        job.addProperty("data", paymentUrl);
//        Gson gson = new Gson();
//        resp.getWriter().write(gson.toJson(job));
        return ResponseEntity.status(HttpStatus.OK).body(paymentResDTO);
    }

    @GetMapping("/pay")
    public String getPay() throws UnsupportedEncodingException{

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = 10000*100;
        String bankCode = "NCB";

        String vnp_TxnRef = VnpayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1";

        String vnp_TmnCode = VnpayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnpayConfig.vnp_Url + "?" + queryUrl;

        return paymentUrl;
    }
//    @PostMapping("/vnpay-ipn")
//    public ResponseEntity<String> vnpayIpn(HttpServletRequest request) {
//        Map<String, String> fields = new HashMap<>();
//        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
//            fields.put(entry.getKey(), entry.getValue()[0]);
//        }
//
//        String vnp_SecureHash = fields.remove("vnp_SecureHash");
//        fields.remove("vnp_SecureHashType"); // Nếu có
//
//        // 1. Sắp xếp key tăng dần
//        List<String> fieldNames = new ArrayList<>(fields.keySet());
//        Collections.sort(fieldNames);
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < fieldNames.size(); i++) {
//            String key = fieldNames.get(i);
//            String value = fields.get(key);
//            sb.append(key).append("=").append(value);
//            if (i < fieldNames.size() - 1) sb.append("&");
//        }
//
//        // 2. Tạo lại chữ ký
//        String myChecksum = VnpayConfig.hmacSHA512(VnpayConfig.secretKey, sb.toString());
//
//        // 3. Log debug
//        System.out.println("--- VNPAY IPN ---");
//        fields.forEach((k, v) -> System.out.println(k + " = " + v));
//        System.out.println("Chuỗi hash: " + sb.toString());
//        System.out.println("My Checksum: " + myChecksum);
//        System.out.println("VNPAY Checksum: " + vnp_SecureHash);
//
//        // 4. So sánh
//        if (myChecksum.equalsIgnoreCase(vnp_SecureHash)) {
//            System.out.println("IPN hợp lệ!");
//            // TODO: Xử lý cập nhật đơn hàng ở đây
//            return ResponseEntity.ok("OK");
//        } else {
//            System.out.println("IPN KHÔNG hợp lệ!");
//            return ResponseEntity.status(400).body("INVALID CHECKSUM");
//        }
//    }
}
