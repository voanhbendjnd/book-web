package djnd.ben1607.drink_shop.mapper;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Category;
import djnd.ben1607.drink_shop.domain.request.BookDTO;
import djnd.ben1607.drink_shop.domain.response.book.ResBook;
import djnd.ben1607.drink_shop.domain.response.book.ResCreateBook;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for Book entity transformations
 * 
 * @Mapper: Đánh dấu interface này là MapStruct mapper
 * @Component: Đăng ký với Spring để có thể inject
 */

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface BookMapper {

    /**
     * Convert Book entity to BookDTO
     * 
     * @param book Book entity
     * @return BookDTO
     */
    @Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
    BookDTO toBookDTO(Book book);

    /**
     * Convert BookDTO to Book entity
     * 
     * @param bookDTO BookDTO
     * @return Book entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true) // Sẽ được xử lý riêng trong service
    @Mapping(target = "bookImages", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "sold", constant = "0")
    @Mapping(target = "coverImage", ignore = true)
    Book toBook(BookDTO bookDTO);

    /**
     * Update existing Book entity with data from BookDTO
     * 
     * @param bookDTO BookDTO
     * @param book    Existing Book entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categories", ignore = true) // Sẽ được xử lý riêng trong service
    @Mapping(target = "bookImages", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "cartItems", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "sold", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "coverImage", ignore = true)
    void updateBookFromDTO(BookDTO bookDTO, @MappingTarget Book book);

    /**
     * Convert Book entity to ResBook (Response DTO)
     * Thay thế cho ConvertModuleBook.fetch()
     * 
     * @param book Book entity
     * @return ResBook
     */
    @Mapping(target = "sold", expression = "java(mapSoldToDouble(book.getSold()))")
    @Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
    @Mapping(target = "imgs", expression = "java(mapBookImagesToStrings(book.getBookImages()))")
    @Mapping(target = "totalReviews", ignore = true) // 🚀 Ignore trong MapStruct
    @Mapping(target = "ratingAverage", ignore = true) // 🚀 Ignore trong MapStruct
    ResBook toResBook(Book book);

    /**
     * Convert Book entity to ResCreateBook (Response DTO)
     * Thay thế cho ConvertModuleBook.create()
     * 
     * @param book Book entity
     * @return ResCreateBook
     */
    @Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
    @Mapping(target = "imgs", expression = "java(mapBookImagesToStrings(book.getBookImages()))")
    ResCreateBook toResCreateBook(Book book);

    /**
     * Convert Book entity to ResUpdateBook (Response DTO)
     * Thay thế cho ConvertModuleBook.update()
     * 
     * @param book Book entity
     * @return ResUpdateBook
     */
    @Mapping(target = "categories", expression = "java(mapCategoriesToStrings(book.getCategories()))")
    @Mapping(target = "imgs", expression = "java(mapBookImagesToStrings(book.getBookImages()))")
    djnd.ben1607.drink_shop.domain.response.book.ResUpdateBook toResUpdateBook(Book book);

    /**
     * Convert List of Book entities to List of ResBook
     * 
     * @param books List of Book entities
     * @return List of ResBook
     */
    List<ResBook> toResBookList(List<Book> books);

    /**
     * Helper method to map Category entities to String names
     * 
     * @param categories List of Category entities
     * @return List of category names
     */
    default List<String> mapCategoriesToStrings(List<Category> categories) {
        if (categories == null) {
            return new ArrayList<>();
        }
        return categories.stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to map BookImage entities to String URLs
     * 
     * @param bookImages List of BookImage entities
     * @return List of image URLs
     */
    default List<String> mapBookImagesToStrings(List<djnd.ben1607.drink_shop.domain.entity.BookImage> bookImages) {
        if (bookImages == null) {
            return new ArrayList<>();
        }
        return bookImages.stream()
                .map(djnd.ben1607.drink_shop.domain.entity.BookImage::getImgUrl)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to convert Integer sold to Double
     * 
     * @param sold Integer sold count
     * @return Double sold count
     */
    default Double mapSoldToDouble(Integer sold) {
        return sold != null ? sold.doubleValue() : 0.0;
    }

    /**
     * Helper method to calculate total reviews count
     * 
     * @param reviews List of Review entities
     * @return Total reviews count as Double
     */
    default Double calculateTotalReviews(List<djnd.ben1607.drink_shop.domain.entity.Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return (double) reviews.stream()
                .filter(review -> review != null) // ← Filter out null reviews
                .map(djnd.ben1607.drink_shop.domain.entity.Review::getId)
                .count();
    }

    /**
     * Helper method to calculate rating average
     * 
     * @param reviews List of Review entities
     * @return Average rating as Double
     */
    default Double calculateRatingAverage(List<djnd.ben1607.drink_shop.domain.entity.Review> reviews) {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
        return reviews.stream()
                .filter(review -> review != null && review.getRating() != null) // ← Filter null reviews and ratings
                .mapToDouble(djnd.ben1607.drink_shop.domain.entity.Review::getRating)
                .average()
                .orElse(0.0);
    }

    /**
     * Helper method to map String names to Category entities
     * Note: This will be handled in the service layer with proper category lookup
     * 
     * @param categoryNames List of category names
     * @return List of Category entities (empty - to be populated in service)
     */
    default List<Category> mapStringsToCategories(List<String> categoryNames) {
        // Trả về null, service layer sẽ xử lý việc tìm và set categories
        return null;
    }
}
