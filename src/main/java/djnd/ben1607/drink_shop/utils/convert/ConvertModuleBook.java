package djnd.ben1607.drink_shop.utils.convert;

import java.util.stream.Collectors;

import djnd.ben1607.drink_shop.domain.entity.Book;
import djnd.ben1607.drink_shop.domain.entity.Review;
import djnd.ben1607.drink_shop.domain.response.book.ResBook;
import djnd.ben1607.drink_shop.domain.response.book.ResCreateBook;
import djnd.ben1607.drink_shop.domain.response.book.ResUpdateBook;

public class ConvertModuleBook {
    public static ResCreateBook create(Book book) {
        ResCreateBook res = new ResCreateBook();
        res.setId(book.getId());
        res.setAuthor(book.getAuthor());
        res.setCoverImage(book.getCoverImage());
        res.setDescription(book.getDescription());
        res.setIsbn(book.getIsbn());
        res.setLanguage(book.getLanguage());
        res.setNumberOfPages(book.getNumberOfPages());
        res.setPrice(book.getPrice());
        res.setStockQuantity(book.getStockQuantity());
        res.setPublicationDate(book.getPublicationDate());
        res.setTitle(book.getTitle());
        res.setPublisher(book.getPublisher());
        res.setCreatedAt(book.getCreatedAt());
        res.setCreatedBy(book.getCreatedBy());
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            res.setCategories(book.getCategories().stream().map(x -> x.getName()).collect(Collectors.toList()));
        }
        return res;
    }

    public static ResUpdateBook update(Book book) {
        ResUpdateBook res = new ResUpdateBook();
        res.setId(book.getId());

        res.setAuthor(book.getAuthor());
        res.setCoverImage(book.getCoverImage());
        res.setDescription(book.getDescription());
        res.setIsbn(book.getIsbn());
        res.setLanguage(book.getLanguage());
        res.setNumberOfPages(book.getNumberOfPages());
        res.setPrice(book.getPrice());
        res.setTitle(book.getTitle());
        res.setStockQuantity(book.getStockQuantity());
        res.setPublicationDate(book.getPublicationDate());
        res.setPublisher(book.getPublisher());
        res.setUpdatedAt(book.getUpdatedAt());
        res.setUpdatedBy(book.getUpdatedBy());
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            res.setCategories(book.getCategories().stream().map(x -> x.getName()).collect(Collectors.toList()));
        }
        return res;
    }

    public static ResBook fetch(Book book) {
        ResBook res = new ResBook();
        res.setId(book.getId());
        res.setAuthor(book.getAuthor());
        res.setCoverImage(book.getCoverImage());
        res.setDescription(book.getDescription());
        res.setIsbn(book.getIsbn());
        res.setTitle(book.getTitle());
        res.setLanguage(book.getLanguage());
        res.setNumberOfPages(book.getNumberOfPages());
        res.setPrice(book.getPrice());
        res.setStockQuantity(book.getStockQuantity());
        res.setPublicationDate(book.getPublicationDate());
        res.setPublisher(book.getPublisher());
        res.setUpdatedAt(book.getUpdatedAt());
        res.setUpdatedBy(book.getUpdatedBy());
        res.setCreatedAt(book.getCreatedAt());
        res.setCreatedBy(book.getCreatedBy());
        if (book.getCategories() != null && !book.getCategories().isEmpty()) {
            res.setCategories(book.getCategories().stream().map(x -> x.getName()).collect(Collectors.toList()));
        }
        res.setTotalReviews((double) book.getReviews().stream().map(Review::getId).count());
        Double ratingAverageAllReview = book.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        res.setRatingAverage(ratingAverageAllReview);
        return res;
    }
}
