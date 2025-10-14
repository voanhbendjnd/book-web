package djnd.ben1607.drink_shop.mapper;

import djnd.ben1607.drink_shop.domain.entity.Category;
import djnd.ben1607.drink_shop.domain.request.CategoryDTO;
import djnd.ben1607.drink_shop.domain.response.category.ResCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResCreateCategory;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for Category entity transformations
 * 
 * @Mapper: Đánh dấu interface này là MapStruct mapper
 * @Component: Đăng ký với Spring để có thể inject
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Convert Category entity to CategoryDTO
     * 
     * @param category Category entity
     * @return CategoryDTO
     */
    @Mapping(target = "listOfBook", expression = "java(mapBooksToIds(category.getBooks()))")
    CategoryDTO toCategoryDTO(Category category);

    /**
     * Convert CategoryDTO to Category entity
     * 
     * @param categoryDTO CategoryDTO
     * @return Category entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true) // Sẽ được xử lý riêng trong service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Category toCategory(CategoryDTO categoryDTO);

    /**
     * Update existing Category entity with data from CategoryDTO
     * 
     * @param categoryDTO CategoryDTO
     * @param category    Existing Category entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "books", ignore = true) // Sẽ được xử lý riêng trong service
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateCategoryFromDTO(CategoryDTO categoryDTO, @MappingTarget Category category);

    /**
     * Convert Category entity to ResCategory (Response DTO)
     * 
     * @param category Category entity
     * @return ResCategory
     */
    ResCategory toResCategory(Category category);

    /**
     * Convert Category entity to ResCreateCategory (Response DTO)
     * 
     * @param category Category entity
     * @return ResCreateCategory
     */
    ResCreateCategory toResCreateCategory(Category category);

    /**
     * Convert List of Category entities to List of ResCategory
     * 
     * @param categories List of Category entities
     * @return List of ResCategory
     */
    List<ResCategory> toResCategoryList(List<Category> categories);

    /**
     * Convert List of Category entities to List of CategoryDTO
     * 
     * @param categories List of Category entities
     * @return List of CategoryDTO
     */
    List<CategoryDTO> toCategoryDTOList(List<Category> categories);

    /**
     * Helper method to map Book entities to their IDs
     * 
     * @param books List of Book entities
     * @return List of book IDs
     */
    default List<Long> mapBooksToIds(List<djnd.ben1607.drink_shop.domain.entity.Book> books) {
        if (books == null) {
            return null;
        }
        return books.stream()
                .map(djnd.ben1607.drink_shop.domain.entity.Book::getId)
                .toList();
    }
}
