package djnd.ben1607.drink_shop.utils.convert;

import djnd.ben1607.drink_shop.domain.entity.Role;
import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.domain.response.user.ResFetchUser;
import djnd.ben1607.drink_shop.domain.response.user.ResUpdateUser;
import djnd.ben1607.drink_shop.repository.RoleRepository;

public class ConvertModuleUser {
    private final RoleRepository roleRepository;

    public ConvertModuleUser(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static ResCreateUser createdTran(User user) {
        ResCreateUser res = new ResCreateUser();
        res.setAddress(user.getAddress());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setPhone(user.getPhone());
        res.setGender(user.getGender());
        res.setCreatedAt(user.getCreatedAt());
        res.setCreatedBy(user.getCreatedBy());
        return res;
    }

    public static ResUpdateUser updateTran(User user) {
        ResUpdateUser res = new ResUpdateUser();
        res.setAddress(user.getAddress());
        res.setName(user.getName());
        res.setEmail(user.getEmail());
        res.setGender(user.getGender());
        res.setPhone(user.getPhone());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setUpdatedBy(user.getUpdatedBy());
        return res;
    }

    public static ResFetchUser fetchUser(User user) {
        ResFetchUser lastUser = new ResFetchUser();
        lastUser.setId(user.getId());
        lastUser.setName(user.getName());
        lastUser.setAddress(user.getAddress());
        lastUser.setGender(user.getGender());
        lastUser.setEmail(user.getEmail());
        lastUser.setPhone(user.getPhone());
        lastUser.setAvatar(user.getAvatar());
        lastUser.setCreatedAt(user.getCreatedAt());
        lastUser.setCreatedBy(user.getCreatedBy());
        lastUser.setUpdatedAt(user.getUpdatedAt());
        lastUser.setUpdatedBy(user.getUpdatedBy());
        lastUser.setRole(user.getRole().getName());
        return lastUser;
    }

    public User convertRequestUserToUser(CreateAccountDTO request) {
        User user = new User();
        user.setAddress(request.getAddress());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setName(request.getName());
        Role role = this.roleRepository.findByName("USER");
        user.setRole(role);
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

}
