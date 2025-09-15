package djnd.ben1607.drink_shop.domain.request;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileUploadRequest {
    private MultipartFile file;
    private String folder;
    private String email;
    private Long bookId;
    // Getters và setters cho tất cả các trường
    // Bạn cũng có thể cần thêm các hàm khởi tạo
}