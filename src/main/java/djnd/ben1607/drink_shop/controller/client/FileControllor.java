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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import djnd.ben1607.drink_shop.domain.request.FileUploadRequest;
import djnd.ben1607.drink_shop.domain.response.files.UploadFileResponse;
import djnd.ben1607.drink_shop.service.FileService;
import djnd.ben1607.drink_shop.utils.SecurityUtils;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileControllor {
    @Value("${djnd.upload-file.base-uri}")
    private String baseURI;
    private final FileService fileService;

    public FileControllor(FileService fileService) {
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

    @PostMapping("/files/upload/avatdsaar")
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
