package djnd.ben1607.drink_shop.domain.response.permission;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreatePermission {
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String createdBy;
    private Instant createdAt;

}
