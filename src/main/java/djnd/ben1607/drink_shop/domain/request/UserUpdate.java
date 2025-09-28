package djnd.ben1607.drink_shop.domain.request;

import djnd.ben1607.drink_shop.utils.constant.GenderEnum;

public record UserUpdate(Long id, String name, String email, String phone, String address, GenderEnum gender,
        String avatar) {

}
