package djnd.ben1607.drink_shop.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import djnd.ben1607.drink_shop.config.VnpayConfig;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.net.URLEncoder;

public class VnpayUtils {

    // 1. Hàm tạo HMAC SHA512 (Bắt buộc phải dùng)
    public static String hmacSHA512(final String key, final String data) {
        try {
            Mac hmacSHA512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmacSHA512.init(secretKey);
            byte[] hash = hmacSHA512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // Chuyển kết quả sang định dạng Hex String
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // Nên sử dụng logging framework thực tế
            System.err.println("Error generating hash: " + e.getMessage());
            throw new RuntimeException("Error generating VNPAY hash", e);
        }
    }

    // 2. Hàm xây dựng URL thanh toán và tạo Secure Hash
    public static String buildPaymentUrl(double amount, String txnRef, String ipAddr)
            throws UnsupportedEncodingException {

        // Chuẩn bị các tham số
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VnpayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnpayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Đơn vị: Xu
        vnp_Params.put("vnp_CurrCode", VnpayConfig.vnp_CurrCode);
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + txnRef);
        vnp_Params.put("vnp_OrderType", VnpayConfig.vnp_OrderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", VnpayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", ipAddr);

        // Thiết lập ngày tạo
        Calendar cld = Calendar.getInstance(VnpayConfig.timeZone);
        String vnp_CreateDate = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // Tạo chuỗi tham số và chuỗi dữ liệu thô (hashData)
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(fieldValue);
                query.append(fieldName).append("=")
                        .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String vnp_SecureHash = hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());
        String queryUrl = VnpayConfig.vnp_PayUrl + "?" + query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

        return queryUrl;
    }

    // 3. Hàm kiểm tra Secure Hash từ VNPAY gửi về
    public static boolean validateSecureHash(Map<String, String> fields) {
        String secureHash = fields.get("vnp_SecureHash");

        // Loại bỏ SecureHash khỏi map
        if (fields.containsKey("vnp_SecureHashType")) {
            fields.remove("vnp_SecureHashType");
        }
        if (fields.containsKey("vnp_SecureHash")) {
            fields.remove("vnp_SecureHash");
        }

        // Sắp xếp lại và tạo chuỗi dữ liệu thô
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append("=").append(fieldValue).append("&");
            }
        }
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1); // Xóa ký tự '&' cuối cùng
        }

        String calculatedHash = hmacSHA512(VnpayConfig.vnp_HashSecret, hashData.toString());

        return calculatedHash.equalsIgnoreCase(secureHash);
    }
}