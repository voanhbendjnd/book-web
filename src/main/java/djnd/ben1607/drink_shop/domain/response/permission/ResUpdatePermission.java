package djnd.ben1607.drink_shop.domain.response.permission;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResUpdatePermission {
    private String name;
    private String apiPath;
    private String method;
    private String module;
    private String updatedBy;
    private Instant updatedAt;
}
