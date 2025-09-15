package djnd.ben1607.drink_shop.domain.response.role;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResCreateRole {
    private String name;
    private String description;
    private Boolean active;
    private String createdBy;
    private Instant createdAt;
    private List<Permissions> permissions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Permissions {
        private Long id;
        private String name;
    }
}
