package djnd.ben1607.drink_shop.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.permission.ResCreatePermission;
import djnd.ben1607.drink_shop.domain.response.permission.ResUpdatePermission;
import djnd.ben1607.drink_shop.service.PermissionService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create new permission")
    public ResponseEntity<ResCreatePermission> createPermission(@Valid @RequestBody Permission per)
            throws IdInvalidException {
        if (this.permissionService.existsPermissionByApiPathAndMethod(per.getApiPath(), per.getMethod())) {
            throw new IdInvalidException(">>> Permission with Apipath: " + per.getApiPath() + " and method: "
                    + per.getMethod() + " is exists! <<<");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.createPermission(per));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<ResUpdatePermission> updatePermission(@RequestBody Permission per)
            throws IdInvalidException {
        if (this.permissionService.existsById(per.getId())) {
            return ResponseEntity.ok(this.permissionService.updatePermission(per));
        }
        throw new IdInvalidException(">>> Permission with ID: " + per.getId() + " is not exists! <<<");
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Fetch permission by ID")
    public ResponseEntity<Permission> fetchPermissionByID(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.permissionService.existsById(id)) {
            return ResponseEntity.ok(permissionService.fetchPermissionByID(id));
        }
        throw new IdInvalidException(">>> Permission with ID: " + id + " is not exits! <<<");
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission by ID")
    public ResponseEntity<Void> deletePermissionByID(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.permissionService.existsById(id)) {
            this.permissionService.deletePermissionByID(id);
            return ResponseEntity.ok(null);

        }
        throw new IdInvalidException(">>> Permission with ID: " + id + " is not exists! <<<");
    }

    @GetMapping("/permissions")
    @ApiMessage("Fetch all permission")
    public ResponseEntity<ResultPaginationDTO> fetchAllPermission(@Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.permissionService.fetchAllPermission(spec, pageable));
    }
}
