package djnd.ben1607.drink_shop.domain.request;

import java.util.List;

import org.hibernate.internal.build.AllowPrintStacktrace;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllowPrintStacktrace
public class CreateRoleDTO {
    private Long id;

    private String name;
    private String description;
    private Boolean active;
    private List<Permission> permissions;

}
