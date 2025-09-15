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

import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.domain.request.CreateRoleDTO;
import djnd.ben1607.drink_shop.domain.request.UpdateRoleDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.role.ResCreateRole;
import djnd.ben1607.drink_shop.domain.response.role.ResUpdateRole;
import djnd.ben1607.drink_shop.service.RoleService;
import djnd.ben1607.drink_shop.utils.annotation.ApiMessage;
import djnd.ben1607.drink_shop.utils.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create new role")
    public ResponseEntity<ResCreateRole> create(@RequestBody CreateRoleDTO role) throws IdInvalidException {
        if (this.roleService.existsByName(role.getName())) {
            throw new IdInvalidException(">>> Name role with: " + role.getName() + " is already exists! <<<");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.createNewRole(role));
    }

    @PutMapping("/roles")
    @ApiMessage("Update role")
    public ResponseEntity<ResUpdateRole> update(@RequestBody UpdateRoleDTO role) throws IdInvalidException {
        if (this.roleService.existsById(role.getId())) {
            return ResponseEntity.ok(this.roleService.updateRole(role));
        }
        throw new IdInvalidException(">>> Role with ID: " + role.getId() + " is not exists! <<<");
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by ID")
    public ResponseEntity<Role> fetchRoleByID(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.roleService.existsById(id)) {
            return ResponseEntity.ok(this.roleService.fetchRoleByID(id));
        }
        throw new IdInvalidException(">>> Role with ID: " + id + " is not exists! <<<");

    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role by  id")
    public ResponseEntity<Void> deleteRoleByID(@PathVariable("id") Long id) throws IdInvalidException {
        if (this.roleService.existsById(id)) {
            this.roleService.deleteRoleByID(id);
            return ResponseEntity.ok(null);
        }
        throw new IdInvalidException(">>> Role with ID: " + id + " is not exists! <<<");

    }

    @GetMapping("/roles")
    @ApiMessage("Fetch all role")
    public ResponseEntity<ResultPaginationDTO> fetchAllRole(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.fetchAllRole(spec, pageable));
    }
}
