package djnd.ben1607.drink_shop.config;

import java.util.TimeZone;

public class VnpayConfig {
    // Tên của bạn nên được thay đổi thành tên dự án của bạn
    public static final String vnp_PayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.htm";
    public static final String vnp_TmnCode = "MÃ_MERCHANT_SANDBOX"; // Thay bằng mã TmnCode VNPAY cấp
    public static final String vnp_HashSecret = "KHOA_BI_MAT_SANDBOX"; // Thay bằng HashSecret VNPAY cấp
    public static final String vnp_ApiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

    // URL này cần trỏ đến Controller của bạn để nhận kết quả thanh toán
    public static final String vnp_ReturnUrl = "http://localhost:8080/api/v1/payment/vnpay-return";

    public static final String vnp_Version = "2.1.0";
    public static final String vnp_CurrCode = "VND";
    public static final String vnp_OrderType = "other";
    public static final TimeZone timeZone = TimeZone.getTimeZone("Etc/GMT+7");
}