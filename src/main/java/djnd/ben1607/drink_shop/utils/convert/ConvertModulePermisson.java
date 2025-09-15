package djnd.ben1607.drink_shop.utils.convert;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.response.permission.ResCreatePermission;
import djnd.ben1607.drink_shop.domain.response.permission.ResUpdatePermission;

public class ConvertModulePermisson {
    public static ResCreatePermission createTran(Permission permission) {
        ResCreatePermission per = new ResCreatePermission();
        per.setApiPath(permission.getApiPath());
        per.setCreatedAt(permission.getCreatedAt());
        per.setCreatedBy(permission.getCreatedBy());
        per.setMethod(permission.getMethod());
        per.setModule(permission.getModule());
        per.setName(permission.getName());
        return per;
    }

    public static ResUpdatePermission updateTran(Permission permission) {
        ResUpdatePermission per = new ResUpdatePermission();
        per.setApiPath(permission.getApiPath());
        per.setMethod(permission.getMethod());
        per.setModule(permission.getModule());
        per.setName(permission.getName());
        per.setUpdatedAt(permission.getUpdatedAt());
        per.setUpdatedBy(permission.getUpdatedBy());
        return per;
    }
}
