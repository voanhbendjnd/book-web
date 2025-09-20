package djnd.ben1607.drink_shop.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.BookImage;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.response.files.UploadFileResponse;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.UserRepository;
import djnd.ben1607.drink_shop.utils.error.EillegalStateException;

@Service
public class FileService {
    @Value("${djnd.upload-file.base-uri}")
    private String baseURI;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public FileService(UserRepository userRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public void createUploadFolder(String folder) throws URISyntaxException, IOException {
        Path path = Paths.get(folder);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + path);
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");

        }
    }

    public UploadFileResponse store(MultipartFile file, String folder, String email) throws URISyntaxException,
            IOException {
        // -> Create link for folder upload
        String uploadPath = baseURI + folder;
        // -> Create folder if folder not exist
        Path directoryPath = Paths.get(uploadPath);
        Files.createDirectories(directoryPath);

        // create unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unnamed";
        }
        String finalName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(originalFilename);
        // URI uri = new URI(baseURI + folder + "/" + finalName);

        // -> Combined between link and file name
        Path filePath = directoryPath.resolve(finalName);

        // Path path = Paths.get(folder, finalName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        // User user =
        // this.userRepository.findByEmail(SecurityUtils.getCurrentUserLogin().get());
        User user = this.userRepository.findByEmail(email);
        user.setAvatar(finalName);
        this.userRepository.save(user);
        return new UploadFileResponse(finalName, Instant.now());
    }

    public UploadFileResponse storeProduct(MultipartFile file, String folder, Long bookId) throws URISyntaxException,
            IOException {
        // -> Create link for folder upload
        String uploadPath = baseURI + folder;
        // -> Create folder if folder not exist
        Path directoryPath = Paths.get(uploadPath);
        Files.createDirectories(directoryPath);

        // create unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unnamed";
        }
        String finalName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(originalFilename);
        // URI uri = new URI(baseURI + folder + "/" + finalName);

        // -> Combined between link and file name
        Path filePath = directoryPath.resolve(finalName);

        // Path path = Paths.get(folder, finalName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        // User user =
        // this.userRepository.findByEmail(SecurityUtils.getCurrentUserLogin().get());
        Book book = this.bookRepository.findById(bookId).get();
        book.setCoverImage(finalName);
        this.bookRepository.save(book);
        return new UploadFileResponse(finalName, Instant.now());
    }

    public String storeProductCreate(MultipartFile file) throws URISyntaxException,
            IOException {
        // -> Create link for folder upload
        String uploadPath = baseURI + "book";
        // -> Create folder if folder not exist
        Path directoryPath = Paths.get(uploadPath);
        Files.createDirectories(directoryPath);

        // create unique filename
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unnamed";
        }
        String finalName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(originalFilename);
        // URI uri = new URI(baseURI + folder + "/" + finalName);

        // -> Combined between link and file name
        Path filePath = directoryPath.resolve(finalName);

        // Path path = Paths.get(folder, finalName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath,
                    StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    // Method 1: Update cover image only
    public void updateBookCoverImage(MultipartFile file, Long bookId)
            throws IOException, EillegalStateException {

        var book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EillegalStateException("Not found book"));

        if (file != null && !file.isEmpty()) {
            String uploadPath = baseURI + "book";
            Path directoryPath = Paths.get(uploadPath);
            Files.createDirectories(directoryPath);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                originalFilename = "cover.jpg";
            }

            String finalName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(originalFilename);
            Path filePath = directoryPath.resolve(finalName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Xóa ảnh cũ nếu muốn
            // deleteOldCoverImage(book.getCoverImage());

            book.setCoverImage(finalName);
            this.bookRepository.save(book);
        }
    }

    // Method 2: Update slider images only
    public void updateBookSliderImages(List<MultipartFile> imgs, Long bookId)
            throws IOException, EillegalStateException {

        var book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new EillegalStateException("Not found book"));

        if (imgs != null && !imgs.isEmpty()) {
            String uploadPath = baseURI + "book";
            Path directoryPath = Paths.get(uploadPath);
            Files.createDirectories(directoryPath);

            // Xóa ảnh cũ nếu muốn
            // deleteOldSliderImages(book.getBookImages());

            var bookImages = new ArrayList<BookImage>();

            for (var img : imgs) {
                if (img != null && !img.isEmpty()) {
                    String originalName = img.getOriginalFilename();
                    if (originalName == null) {
                        originalName = "image.jpg";
                    }

                    String finalName = System.currentTimeMillis() + "-" + StringUtils.cleanPath(originalName);
                    Path imgPath = directoryPath.resolve(finalName);

                    try (InputStream inputStream = img.getInputStream()) {
                        Files.copy(inputStream, imgPath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    var bookImage = new BookImage();
                    bookImage.setImgUrl(finalName);
                    bookImage.setBook(book);
                    bookImages.add(bookImage);

                    // Đảm bảo timestamp khác nhau
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }

            book.getBookImages().clear(); // Xóa ảnh cũ
            book.setBookImages(bookImages);
            this.bookRepository.save(book);
        }
    }

    // Method 3: Update both (combination of above)
    public void updateBookImages(MultipartFile coverImage, List<MultipartFile> sliderImages, Long bookId)
            throws IOException, EillegalStateException {

        if (coverImage != null && !coverImage.isEmpty()) {
            updateBookCoverImage(coverImage, bookId);
        }

        if (sliderImages != null && !sliderImages.isEmpty()) {
            updateBookSliderImages(sliderImages, bookId);
        }
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        URI uri = new URI(baseURI + folder + "/" + fileName);
        Path path = Paths.get(uri.toString());

        File tmpDir = new File(path.toString());

        // file không tồn tại, hoặc file là 1 director => return 0
        if (!tmpDir.exists() || tmpDir.isDirectory())
            return 0;
        return tmpDir.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        // URI uri = new URI(baseURI + folder + "/" + fileName);
        // Path path = Paths.get(uri);
        Path path = Paths.get(baseURI, folder + "/", fileName);

        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
