package djnd.ben1607.drink_shop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import djnd.ben1607.drink_shop.domain.entity.Permission;
import djnd.ben1607.drink_shop.domain.response.ResultPaginationDTO;
import djnd.ben1607.drink_shop.domain.response.permission.ResCreatePermission;
import djnd.ben1607.drink_shop.domain.response.permission.ResUpdatePermission;
import djnd.ben1607.drink_shop.repository.PermissionRepository;
import djnd.ben1607.drink_shop.utils.ChangeUpdate;
import djnd.ben1607.drink_shop.utils.convert.ConvertModulePermisson;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean existsById(Long id) {
        return this.permissionRepository.existsById(id);
    }

    public boolean existsPermissionByApiPathAndMethod(String api, String method) {
        return this.permissionRepository.existsPermissionByApiPathAndMethod(api, method);
    }

    public List<Permission> fetchPermissionByIdIn(List<Long> ids) {
        return this.permissionRepository.findByIdIn(ids) != null ? this.permissionRepository.findByIdIn(ids) : null;
    }

    public ResCreatePermission createPermission(Permission per) {
        Permission lastPer = this.permissionRepository.save(per);
        return ConvertModulePermisson.createTran(lastPer);
    }

    public ResUpdatePermission updatePermission(Permission per) {
        Permission perDB = this.permissionRepository.findById(per.getId()).get();
        ChangeUpdate.handle(per, perDB);
        Permission lastPer = this.permissionRepository.save(perDB);
        return ConvertModulePermisson.updateTran(lastPer);
    }

    public Permission fetchPermissionByID(Long id) {
        return this.permissionRepository.findById(id).get();
    }

    public void deletePermissionByID(Long id) {
        Optional<Permission> permissionOptional = this.permissionRepository.findById(id);
        if(permissionOptional.isPresent()){
            permissionOptional.get().getRoles()
                    .forEach(x -> x.getPermissions().remove(permissionOptional.get()));
            this.permissionRepository.delete(permissionOptional.get());
        }
      

    }

    public ResultPaginationDTO fetchAllPermission(Specification<Permission> spec, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        Page<Permission> page = this.permissionRepository.findAll(pageable);
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());
        res.setMeta(mt);
        res.setResult(page.getContent());
        return res;
    }
}
