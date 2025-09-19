package djnd.ben1607.drink_shop.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import djnd.ben1607.drink_shop.domain.request.CategoryDTO;
import djnd.ben1607.drink_shop.domain.response.category.ResCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResCreateCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResUpdateCategory;
import djnd.ben1607.drink_shop.repository.CategoryRepository;
import djnd.ben1607.drink_shop.service.CategoryService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryService categoryService, CategoryRepository categoryRepository) {
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping("/categories")
    @ApiMessage("Create new category")
    public ResponseEntity<ResCreateCategory> create(@Valid @RequestBody CategoryDTO dto) throws IdInvalidException {
        if (!this.categoryRepository.existsByName(dto.getName())) {
            return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.create(dto));
        }
        throw new IdInvalidException(">>> Category with name (" + dto.getName()
                + ") already exists!, please input other name for Category <<<");
    }

    @PutMapping("/categories")
    @ApiMessage("Update category")
    public ResponseEntity<ResUpdateCategory> update(@Valid @RequestBody CategoryDTO dto) throws IdInvalidException {
        if (this.categoryRepository.existsById(dto.getId())) {
            if (dto.getName() != null && this.categoryRepository.existsByName(dto.getName())) {
                throw new IdInvalidException(">>> Category with name(" + dto.getName()
                        + ") already exists!, please input other name for Category <<<");
            } else {
                return ResponseEntity.ok(this.categoryService.update(dto));
            }
        }
        throw new IdInvalidException(">>> Id category with (" + dto.getId() + ") is not exists! <<<");
    }

    @GetMapping("/categories/{id}")
    @ApiMessage("Fetch category by ID")
    public ResponseEntity<ResCategory> fetchById(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.categoryRepository.existsById(id)) {
            return ResponseEntity.ok(this.categoryService.fetchById(id));
        }
        throw new IdInvalidException(">>> ID category with (" + id + ") is not exists! <<<");
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete category by ID")
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.categoryRepository.existsById(id)) {
            this.categoryService.deleteById(id);
            return ResponseEntity.ok(null);
        }
        throw new IdInvalidException(">>> ID category with (" + id + ") is not exists! <<<");
    }

    // @GetMapping("/categories")
    // @ApiMessage("Fetch all category")
    // public ResponseEntity<ResultPaginationDTO> fetchAll(@Filter
    // Specification<Category> spec, Pageable pageable) {
    // return ResponseEntity.ok(this.categoryService.fetchAll(spec, pageable));
    // }

    @GetMapping("/categories")
    @ApiMessage("Fetch all name category")
    public ResponseEntity<List<String>> fetchAllNameCategories() {
        return ResponseEntity.ok(this.categoryService.getAllNameCategories());
    }

}
