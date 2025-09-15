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
public class ResFetchUser {
    private Long id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private GenderEnum gender;
    private String avatar;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private String role;
}
