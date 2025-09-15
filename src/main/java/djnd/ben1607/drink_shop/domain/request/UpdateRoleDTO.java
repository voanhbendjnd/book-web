package djnd.ben1607.drink_shop.domain.request;

import java.util.List;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRoleDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private List<Permission> listIDPermission;
    private String condition;
}
