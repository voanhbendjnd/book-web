package djnd.ben1607.drink_shop.utils.convert;

import djnd.ben1607.drink_shop.domain.entity.Review;
import djnd.ben1607.drink_shop.domain.response.review.ResCreateReview;
import djnd.ben1607.drink_shop.domain.response.review.ResReview;
import djnd.ben1607.drink_shop.domain.response.review.ResUpdateReview;

public class ConvertModuleReview {
    public static ResCreateReview create(Review review) {
        ResCreateReview res = new ResCreateReview();
        res.setComment(review.getComment());
        res.setId(review.getId());
        res.setCreatedAt(review.getCreatedAt());
        res.setCreatedBy(review.getCreatedBy());
        res.setRating(review.getRating());
        res.setReviewDate(review.getReviewDate());
        res.setTitleBook(review.getBook().getTitle());
        res.setUsername(review.getUser().getName());
        return res;
    }

    public static ResUpdateReview update(Review review) {
        ResUpdateReview res = new ResUpdateReview();
        res.setComment(review.getComment());
        res.setId(review.getId());
        res.setUpdatedAt(review.getUpdatedAt());
        res.setUpdatedBy(review.getUpdatedBy());
        res.setRating(review.getRating());
        res.setReviewDate(review.getReviewDate());
        res.setTitleBook(review.getBook().getTitle());
        res.setUsername(review.getUser().getName());
        return res;
    }

    public static ResReview fetch(Review review) {
        ResReview res = new ResReview();
        res.setComment(review.getComment());
        res.setId(review.getId());
        res.setUpdatedAt(review.getUpdatedAt());
        res.setUpdatedBy(review.getUpdatedBy());
        res.setCreatedAt(review.getCreatedAt());
        res.setCreatedBy(review.getCreatedBy());
        res.setRating(review.getRating());
        res.setReviewDate(review.getReviewDate());
        res.setTitleBook(review.getBook().getTitle());
        res.setUsername(review.getUser().getName());
        return res;
    }
}
