package djnd.ben1607.drink_shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import djnd.ben1607.drink_shop.repository.OrderRepository;
import djnd.ben1607.drink_shop.utils.VnpayUtils;
import djnd.ben1607.drink_shop.utils.constant.OrderStatusEnum;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;

    // Constructor injection
    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    // 1. Tạo URL thanh toán
    public String initiateVnpayPayment(Long orderId, String ipAddr) throws UnsupportedEncodingException {
        // Giả định bạn đã có logic tạo Order và lưu tạm thời
        var order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Dùng VnpayUtils để tạo URL
        double amount = order.getTotalAmount();
        return VnpayUtils.buildPaymentUrl(amount, String.valueOf(orderId), ipAddr);
    }

    // 2. Xử lý Callback (Rất quan trọng)
    @Transactional
    public boolean handleVnpayReturn(Map<String, String> vnpayParams) {

        // BƯỚC 1: KIỂM TRA BẢO MẬT (Bắt buộc)
        if (!VnpayUtils.validateSecureHash(vnpayParams)) {
            // Giao dịch không hợp lệ/bị giả mạo! Cần log lại sự kiện này.
            return false;
        }

        // BƯỚC 2: KIỂM TRA MÃ GIAO DỊCH
        String txnRef = vnpayParams.get("vnp_TxnRef");
        String responseCode = vnpayParams.get("vnp_ResponseCode");
        String transactionStatus = vnpayParams.get("vnp_TransactionStatus");

        var order = orderRepository.findById(Long.valueOf(txnRef))
                .orElseThrow(() -> new RuntimeException("Order not found during VNPAY callback"));

        // BƯỚC 3: CẬP NHẬT TRẠNG THÁI ORDER
        if (order.getStatus().equals(OrderStatusEnum.PENDING)) { // Chỉ xử lý khi trạng thái đang chờ
            if ("00".equals(responseCode) && "00".equals(transactionStatus)) {
                // Giao dịch thành công
                order.setStatus(OrderStatusEnum.PAID);
            } else if ("24".equals(responseCode)) {
                // Người dùng hủy/giao dịch hết hạn
                order.setStatus(OrderStatusEnum.CANCELED);
                // Cần thêm logic hoàn lại tồn kho nếu bạn đã trừ trước đó
            } else {
                // Các lỗi khác
                order.setStatus(OrderStatusEnum.FAILED);
            }
            orderRepository.save(order); // Tự động lưu do @Transactional
            return true;
        }

        // Trường hợp giao dịch đã được xử lý trước đó (ví dụ: callback gửi lại lần 2)
        return true;
    }
}