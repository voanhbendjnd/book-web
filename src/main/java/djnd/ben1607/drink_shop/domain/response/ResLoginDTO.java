package djnd.ben1607.drink_shop.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import djnd.ben1607.drink_shop.utils.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ResLoginDTO {
    @JsonProperty("access_token")
    private String accessToken;
    private UserLogin user;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    // inner class
    public static class UserLogin {
        private Long id;
        private String email;
        private String name;
        private String avatar;
        private String address;
        private String phone;
        private String role;
        private GenderEnum gender;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    // inner class
    public static class UserGetAccount {
        private UserLogin user;
    }

    // save information inside token (not save permission)
    // use for security
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    // inner class
    public static class UserInsideToken {
        private Long id;
        private String email;
        private String name;
    }

}
