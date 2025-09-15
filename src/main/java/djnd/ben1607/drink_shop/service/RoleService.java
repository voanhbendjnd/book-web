package djnd.ben1607.drink_shop.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.domain.request.CreateRoleDTO;
import djnd.ben1607.drink_shop.domain.request.UpdateRoleDTO;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.role.ResCreateRole;
import djnd.ben1607.drink_shop.domain.response.role.ResUpdateRole;
import djnd.ben1607.drink_shop.repository.RoleRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleService(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    public boolean existsById(Long id) {
        return this.roleRepository.existsById(id);
    }

    public boolean existsByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public ResCreateRole createNewRole(CreateRoleDTO role) {
        if (role.getPermissions() != null) {
            List<Long> listIDPer = role.getPermissions().stream()
                    .map(Permission::getId)
                    .collect(Collectors.toList());
            List<Permission> listPer = this.permissionService.fetchPermissionByIdIn(listIDPer);
            if (listPer != null) {
                role.setPermissions(listPer);
            }
        }
        Role firstRole = new Role();
        firstRole.setName(role.getName());
        firstRole.setDescription(role.getDescription());
        firstRole.setPermissions(role.getPermissions());
        firstRole.setActive(role.getActive());
        // -> this place save <-
        Role tmpRole = this.roleRepository.save(firstRole);

        ResCreateRole lastRole = new ResCreateRole();
        lastRole.setActive(tmpRole.getActive());
        lastRole.setCreatedAt(tmpRole.getCreatedAt());
        lastRole.setCreatedBy(tmpRole.getCreatedBy());
        lastRole.setDescription(tmpRole.getDescription());
        lastRole.setName(tmpRole.getName());
        List<ResCreateRole.Permissions> lastPerList = tmpRole.getPermissions().stream()
                .map(x -> new ResCreateRole.Permissions(x.getId(), x.getName()))
                .collect(Collectors.toList());
        lastRole.setPermissions(lastPerList);
        return lastRole;
    }

    public ResUpdateRole updateRole(UpdateRoleDTO roles) {
        Role roleDB = this.roleRepository.findById(roles.getId()).get();
        Role role = new Role();
        role.setActive(roles.getActive());
        role.setName(roles.getName());
        role.setId(roles.getId());
        role.setDescription(roles.getDescription());
        if (roles.getListIDPermission() != null) {
            List<Permission> listPer = this.permissionService.fetchPermissionByIdIn(
                    roles.getListIDPermission().stream().map(Permission::getId).collect(Collectors.toList()));
            if (listPer != null) {
                List<Permission> listPerAvaiable = roleDB.getPermissions();
                Set<Long> listIDPerAvaiable = listPerAvaiable.stream()
                        .map(Permission::getId)
                        .collect(Collectors.toSet());

                if (roles.getCondition().equalsIgnoreCase("add_permission")) {
                    List<Permission> listPerNoInclude = listPer.stream()
                            .filter(p -> !listIDPerAvaiable.contains(p.getId()))
                            .collect(Collectors.toList());
                    listPerAvaiable.addAll(listPerNoInclude);
                } else if (roles.getCondition().equalsIgnoreCase("delete_permission")) {
                    List<Permission> listPerNoInclude = listPer.stream()
                            .filter(p -> listIDPerAvaiable.contains(p.getId()))
                            .collect(Collectors.toList());
                    listPerAvaiable.removeAll(listPerNoInclude);
                }

                role.setPermissions(listPerAvaiable);
            }

        }
        ChangeUpdate.handle(role, roleDB);
        Role lastRole = this.roleRepository.save(roleDB);
        ResUpdateRole ru = new ResUpdateRole();
        ru.setActive(lastRole.getActive());
        ru.setName(lastRole.getName());
        ru.setUpdatedAt(lastRole.getUpdatedAt());
        ru.setUpdatedBy(lastRole.getUpdatedBy());
        ru.setDescription(lastRole.getDescription());
        ru.setPermissions(lastRole.getPermissions().stream()
                .map(x -> new ResUpdateRole.Permissions(x.getId(), x.getModule())).collect(Collectors.toList()));
        return ru;

    }

    public void deleteRoleByID(Long id) {
        this.roleRepository.deleteById(id);
    }

    public Role fetchRoleByID(Long id) {
        return this.roleRepository.findById(id).get();
    }

    public ResultPaginationDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(pageable);
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPage(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent());
        return res;
    }

}
