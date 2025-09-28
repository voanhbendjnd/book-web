package djnd.ben1607.drink_shop.controller.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import djnd.ben1607.drink_shop.domain.request.FileUploadRequest;
import djnd.ben1607.drink_shop.domain.response.files.UploadFileResponse;
import djnd.ben1607.drink_shop.service.FileService;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;
import djnd.ben1607.drink_shop.utils.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    @Value("${djnd.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/files/upload/test")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponse> upload(
            @ModelAttribute FileUploadRequest request)
            throws URISyntaxException, IOException, StorageException {
        if (request.getFile() == null || request.getFile().isEmpty()) {
            throw new StorageException("File is empty, please up load a file");
        }
        String fileName = request.getFile().getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png",
                "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(
                item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allows " +
                    allowedExtensions.toString());
        }

        this.fileService.createUploadFolder(baseURI + request.getFolder());

        return ResponseEntity.ok()
                .body(this.fileService.store(request.getFile(), request.getFolder(),
                        SecurityUtils.getCurrentUserLogin().get()));
    }

    @PostMapping("/files/upload/avatar/users")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponse> up(@ModelAttribute FileUploadRequest request)
            throws URISyntaxException, IOException, StorageException {
        MultipartFile file = request.getFile();
        String folder = request.getFolder();
        String email = SecurityUtils.getCurrentUserLogin().isPresent() ? SecurityUtils.getCurrentUserLogin().get() : "";
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please up load a file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(
                item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allows " + allowedExtensions.toString());
        }

        this.fileService.createUploadFolder(baseURI + folder);
        return ResponseEntity.ok().body(this.fileService.store(file, folder, email));
    }

    @PostMapping("/files/upload/image/book")
    @ApiMessage("Upload file book")
    public ResponseEntity<?> uploadImageBook(@RequestPart("coverImage") MultipartFile coverImage,
            @RequestPart("imgs") List<MultipartFile> imgs, @RequestParam Long id)
            throws IOException, EillegalStateException {
        if (coverImage != null) {
            List<String> notFile = Arrays.asList("jpg", "jpeg", "png");
            boolean check = notFile.stream().anyMatch(x -> coverImage.getOriginalFilename().endsWith(x));
            if (!check) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File invalid");
            }

        }
        if (imgs != null && !imgs.isEmpty()) {
            List<String> notFile = Arrays.asList("jpg", "jpeg", "png");
            for (MultipartFile x : imgs) {
                boolean check = notFile.stream().anyMatch(it -> x.getOriginalFilename().toLowerCase().endsWith(it));
                if (!check) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File invalid");
                }
            }
        }
        this.fileService.updateBookImages(coverImage, imgs, id);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Upload image for book successfull");
    }

    @PostMapping("/files/upload/avatar/user")
    @ApiMessage("Update avatar")
    public ResponseEntity<?> avatar(
            @RequestPart("avatar") MultipartFile avatar,
            @RequestParam("id") Long id)
            throws IOException, EillegalStateException {
        if (avatar == null || avatar.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Avatar is required");
        }
        String originalFileName = avatar.getOriginalFilename();
        if (originalFileName == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file name");
        }
        try {
            this.fileService.updateAvatar(avatar, id);
            return ResponseEntity.ok("Upload avatar successfully");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload avatar: " + ex.getMessage());
        }

    }

    @PostMapping("/files/upload/cover-image/book")
    @ApiMessage("Upload cover image for book")
    public ResponseEntity<?> coverImage(
            @RequestPart("coverImage") MultipartFile coverImage,
            @RequestParam("id") Long id) throws IOException, EillegalStateException { // ← Thêm name "id"

        if (coverImage == null || coverImage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cover image is required");
        }

        // Validate file extension
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");
        String originalFilename = coverImage.getOriginalFilename();

        if (originalFilename == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid filename");
        }

        boolean isValidFile = allowedExtensions.stream()
                .anyMatch(ext -> originalFilename.toLowerCase().endsWith("." + ext));

        if (!isValidFile) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File must be jpg, jpeg, or png format");
        }

        try {
            this.fileService.updateBookCoverImage(coverImage, id);
            return ResponseEntity.ok("Upload cover image for book successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload cover image: " + e.getMessage());
        }
    }

    @PostMapping("/files/upload/slider-images/book")
    @ApiMessage("Upload slider images for book")
    public ResponseEntity<?> sliderImages(
            @RequestPart("imgs") List<MultipartFile> imgs,
            @RequestParam("id") Long id) throws IOException, EillegalStateException { // ← Thêm name "id"

        if (imgs == null || imgs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("At least one slider image is required");
        }

        // Validate all files
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png");

        for (MultipartFile img : imgs) {
            if (img.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Empty file detected");
            }

            String originalFilename = img.getOriginalFilename();
            if (originalFilename == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Invalid filename detected");
            }

            boolean isValidFile = allowedExtensions.stream()
                    .anyMatch(ext -> originalFilename.toLowerCase().endsWith("." + ext));

            if (!isValidFile) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("All files must be jpg, jpeg, or png format. Invalid file: " + originalFilename);
            }
        }

        try {
            this.fileService.updateBookSliderImages(imgs, id);
            return ResponseEntity.ok("Upload slider images for book successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload slider images: " + e.getMessage());
        }
    }

    @PostMapping("/files/upload/product")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponse> uploadPictureProduct(@ModelAttribute FileUploadRequest request)
            throws URISyntaxException, IOException, StorageException {
        MultipartFile file = request.getFile();
        String folder = request.getFolder();

        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please up load a file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(
                item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allows " + allowedExtensions.toString());
        }

        this.fileService.createUploadFolder(baseURI + folder);
        return ResponseEntity.ok().body(this.fileService.storeProduct(file, folder, request.getBookId()));
    }

    @PostMapping("/files/upload/avatar")
    @ApiMessage("Upload single file")
    public ResponseEntity<UploadFileResponse> uploadFile(
            @ModelAttribute FileUploadRequest request)
            throws URISyntaxException, IOException, StorageException {
        MultipartFile file = request.getFile();
        String folder = request.getFolder();
        String email = request.getEmail();
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty, please up load a file");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtensions.stream().anyMatch(
                item -> fileName.toLowerCase().endsWith(item));

        if (!isValid) {
            throw new StorageException("Invalid file extension. Only allows " + allowedExtensions.toString());
        }

        this.fileService.createUploadFolder(baseURI + folder);
        return ResponseEntity.ok().body(this.fileService.store(file, folder, email));
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> download(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws StorageException, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params.");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/images/{folder}/{filename:.+}")
    public ResponseEntity<Resource> getFile(
            @PathVariable("folder") String folder,
            @PathVariable("filename") String filename) throws IOException {
        try {
            // Sửa lỗi: Kết hợp đường dẫn gốc, thư mục con và tên file một cách an toàn
            Path filePath = Paths.get("C:/Users/PC/Documents/Backend/Project/upload/", folder, filename).normalize();

            // Tối ưu hóa: Trả về MediaType chính xác để trình duyệt hiển thị ảnh
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = java.nio.file.Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                        .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
