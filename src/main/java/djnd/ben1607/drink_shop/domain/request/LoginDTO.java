package djnd.ben1607.drink_shop.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @NotBlank(message = ">>> User name cannot be empty! <<<")
    private String username;
    @NotBlank(message = ">>> Password cannot be empty! <<<")
    private String password;
}
