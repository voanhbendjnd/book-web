package djnd.ben1607.drink_shop.mapper;

import djnd.ben1607.drink_shop.domain.entity.User;
import djnd.ben1607.drink_shop.domain.request.CreateAccountDTO;
import djnd.ben1607.drink_shop.domain.request.UpdateUserDTO;
import djnd.ben1607.drink_shop.domain.request.UserUpdate;
import djnd.ben1607.drink_shop.domain.response.ResLoginDTO;
import djnd.ben1607.drink_shop.domain.response.user.ResCreateUser;
import djnd.ben1607.drink_shop.domain.response.user.ResFetchUser;

import org.mapstruct.*;

/**
 * MapStruct mapper for User entity transformations
 * 
 * @Mapper: Đánh dấu interface này là MapStruct mapper
 * @Component: Đăng ký với Spring để có thể inject
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION)
public interface UserMapper {

    /**
     * Convert User entity to ResLoginDTO.UserLogin
     * 
     * @param user User entity
     * @return ResLoginDTO.UserLogin DTO
     */
    @Mapping(source = "role.name", target = "role")
    ResLoginDTO.UserLogin toUserLogin(User user);

    /**
     * Convert User entity to ResLoginDTO.UserInsideToken
     * 
     * @param user User entity
     * @return ResLoginDTO.UserInsideToken DTO
     */
    ResLoginDTO.UserInsideToken toUserInsideToken(User user);

    /**
     * Convert CreateAccountDTO to User entity
     * 
     * @param createAccountDTO CreateAccountDTO
     * @return User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "oneTimePassword", ignore = true)
    @Mapping(target = "otpRequestedTime", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(CreateAccountDTO createAccountDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "oneTimePassword", ignore = true)
    @Mapping(target = "otpRequestedTime", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toUser(UserUpdate userUpdate);

    /**
     * Update existing User entity with data from UpdateUserDTO
     * 
     * @param updateUserDTO UpdateUserDTO
     * @param user          Existing User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "sessionId", ignore = true)
    @Mapping(target = "oneTimePassword", ignore = true)
    @Mapping(target = "otpRequestedTime", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateUserFromDTO(UpdateUserDTO updateUserDTO, @MappingTarget User user);

    /**
     * Convert User entity to UserDTO (if exists)
     * 
     * @param user User entity
     * @return UserDTO
     */
    djnd.ben1607.drink_shop.domain.request.UserDTO toUserDTO(User user);

    ResCreateUser toResCreateUser(User user);

    @Mapping(source = "role.name", target = "role")
    ResFetchUser toResFetchUser(User user);
}
