package djnd.ben1607.drink_shop.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Category;
import djnd.ben1607.drink_shop.domain.request.CategoryDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.category.ResCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResCreateCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResUpdateCategory;
import djnd.ben1607.drink_shop.repository.BookRepository;
import djnd.ben1607.drink_shop.repository.CategoryRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;
import djnd.ben1607.drink_shop.utils.convert.ConvertModuleCategory;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryService(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    public List<Category> findByIdIn(List<Long> ids) {
        List<Category> categories = this.categoryRepository.findByIdIn(ids);
        return categories != null ? categories : null;
    }

    public ResCreateCategory create(CategoryDTO dto) {
        Category cate = new Category();
        if (dto.getListOfBook() != null) {
            List<Book> listBookCurrent = this.bookRepository.findByIdIn(dto.getListOfBook());
            if (listBookCurrent != null) {
                cate.setBooks(listBookCurrent);
            }
        }
        cate.setName(dto.getName());
        cate.setDescription(dto.getDescription());
        return ConvertModuleCategory.create(this.categoryRepository.save(cate));
    }

    public ResUpdateCategory update(CategoryDTO dto) {
        Category cateDB = this.categoryRepository.findById(dto.getId()).get();
        if (cateDB != null) {
            Category cate = new Category();
            cate.setName(dto.getName());
            cate.setDescription(dto.getDescription());
            if (dto.getListOfBook() != null) {
                List<Book> listBookCurrent = this.bookRepository.findByIdIn(dto.getListOfBook());
                if (listBookCurrent != null) {
                    cate.setBooks(listBookCurrent);
                }
            }
            ChangeUpdate.handle(cate, cateDB);
            return ConvertModuleCategory.update(this.categoryRepository.save(cateDB));
        }
        return null;
    }

    public ResCategory fetchById(Long id) {
        return ConvertModuleCategory.fetch(this.categoryRepository.findById(id).get());
    }

    public void deleteById(Long id) {
        Category cateDB = this.categoryRepository.findById(id).get();
        cateDB.getBooks().forEach(x -> x.getCategories().remove(cateDB));
        this.categoryRepository.delete(cateDB);
    }

    public ResultPaginationDTO fetchAll(Specification<Category> spec, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        Page<Category> page = this.categoryRepository.findAll(pageable);
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent().stream().map(ConvertModuleCategory::fetch).collect(Collectors.toList()));
        return res;
    }

    public List<String> getAllNameCategories() {
        return this.categoryRepository.findAll().stream().map(Category::getName).collect(Collectors.toList());
    }
}
