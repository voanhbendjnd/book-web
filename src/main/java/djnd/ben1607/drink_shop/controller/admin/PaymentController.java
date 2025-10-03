package djnd.ben1607.drink_shop.controller.admin;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import djnd.ben1607.drink_shop.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1. ENDPOINT KHỞI TẠO THANH TOÁN (INITIATION)
    // Đã sửa từ @PostMapping sang @GetMapping để phù hợp với window.location.href
    // (FE)
    @GetMapping("/vnpay/create/{id}")
    public RedirectView createVnpayPayment(
            @RequestParam("id") Long id,
            HttpServletRequest request)
            throws UnsupportedEncodingException {

        // Lấy IP người dùng
        String ipAddr = request.getRemoteAddr();

        // Gọi Service để tạo URL VNPAY
        String paymentUrl = paymentService.initiateVnpayPayment(id, ipAddr);

        // Trả về lệnh REDIRECT tới URL của VNPAY
        return new RedirectView(paymentUrl);
    }

    // 2. ENDPOINT XỬ LÝ KẾT QUẢ TRẢ VỀ (CALLBACK)
    // VNPAY gọi endpoint này sau khi giao dịch hoàn tất
    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(@RequestParam Map<String, String> vnpayParams) {

        // Gọi Service để xử lý kết quả và cập nhật trạng thái Order
        boolean success = paymentService.handleVnpayReturn(vnpayParams);

        // Lấy mã đơn hàng và mã kết quả VNPAY
        String orderId = vnpayParams.get("vnp_TxnRef");
        String responseCode = vnpayParams.get("vnp_ResponseCode");

        // Trả về mã kết quả VNPAY và Order ID để FE có thể đọc và hiển thị
        if (success && "00".equals(responseCode)) {
            // Thêm các tham số VNPAY vào URL redirect để FE đọc và hiển thị kết quả
            // Ở đây chỉ trả về text, nhưng tốt nhất nên trả về RedirectView đến FE URL
            // Ví dụ: return new
            // RedirectView("http://your-frontend/return?vnp_ResponseCode=00&vnp_TxnRef=" +
            // orderId);

            // Tuy nhiên, theo luồng hiện tại của bạn, BE trả về ResponseEntity.ok()
            // nên chúng ta sẽ trả về mã kết quả để FE có thể đọc sau khi BE xử lý.
            return ResponseEntity.ok("Payment processed. vnp_ResponseCode=" + responseCode + "&vnp_TxnRef=" + orderId);
        } else {
            return ResponseEntity.badRequest()
                    .body("Payment processed. vnp_ResponseCode=" + responseCode + "&vnp_TxnRef=" + orderId);
        }
    }
}
