package djnd.ben1607.drink_shop.utils.convert;

import java.util.List;
import java.util.stream.Collectors;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Category;
import djnd.ben1607.drink_shop.domain.response.category.ResCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResCreateCategory;
import djnd.ben1607.drink_shop.domain.response.category.ResUpdateCategory;

public class ConvertModuleCategory {
    public static ResCreateCategory create(Category cate) {
        ResCreateCategory res = new ResCreateCategory();
        res.setCreatedAt(cate.getCreatedAt());
        res.setCreatedBy(cate.getCreatedBy());
        res.setDescription(cate.getDescription());
        res.setId(cate.getId());
        res.setName(cate.getName());
        List<ResCreateCategory.Book> listOfBook = cate.getBooks().stream()
                .map(x -> new ResCreateCategory.Book(x.getId(), x.getTitle()))
                .collect(Collectors.toList());
        res.setListOfBook(listOfBook);
        return res;
    }

    public static ResUpdateCategory update(Category cate) {
        ResUpdateCategory res = new ResUpdateCategory();
        res.setUpdatedAt(cate.getUpdatedAt());
        res.setUpdatedBy(cate.getUpdatedBy());
        res.setDescription(cate.getDescription());
        res.setId(cate.getId());
        res.setName(cate.getName());
        List<ResUpdateCategory.Book> listOfBook = cate.getBooks().stream()
                .map(x -> new ResUpdateCategory.Book(x.getId(), x.getTitle()))
                .collect(Collectors.toList());
        res.setListOfBook(listOfBook);
        return res;
    }

    public static ResCategory fetch(Category cate) {
        ResCategory res = new ResCategory();
        res.setUpdatedAt(cate.getUpdatedAt());
        res.setUpdatedBy(cate.getUpdatedBy());
        res.setCreatedAt(cate.getCreatedAt());
        res.setCreatedBy(cate.getCreatedBy());
        res.setDescription(cate.getDescription());
        res.setId(cate.getId());
        res.setName(cate.getName());
        List<ResCategory.Book> listOfBook = cate.getBooks().stream()
                .map(x -> new ResCategory.Book(x.getId(), x.getTitle()))
                .collect(Collectors.toList());
        res.setListOfBook(listOfBook);
        res.setTotalBook((int) cate.getBooks().stream().map(Book::getId).count());
        return res;
    }
}
