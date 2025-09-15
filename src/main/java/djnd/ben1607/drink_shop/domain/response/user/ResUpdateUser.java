package djnd.ben1607.drink_shop.domain.response.user;

import java.time.Instant;

import djnd.ben1607.drink_shop.utils.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateUser {
    private String name;
    private String email;
    private String phone;
    private String address;
    private GenderEnum gender;
    private Instant updatedAt;
    private String updatedBy;
}
