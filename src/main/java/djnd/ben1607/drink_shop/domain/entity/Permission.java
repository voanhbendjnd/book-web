package djnd.ben1607.drink_shop.domain.entity;

import java.time.Instant;
import java.util.List;

import djnd.ben1607.drink_shop.utils.SecurityUtils;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "permissions")
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = ">>> Permission name cannot be empty! <<<")
    private String name;
    @NotBlank(message = ">>> Permission api path cannot be empty! <<<")
    private String apiPath;
    @NotBlank(message = ">>> Permission method cannot be empty! <<<")
    private String method;
    @NotBlank(message = ">>> Permission module cannot be empty! <<<")
    private String module;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "permissions") // ánh xạ đến bên role
    private List<Role> roles;

    public Permission(String name, String apiPath, String method, String module) {
        this.apiPath = apiPath;
        this.method = method;
        this.module = module;
        this.name = name;
    }

    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void handleBeforeCreateAt() {
        this.createdBy = SecurityUtils.getCurrentUserLogin().isPresent() == true
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        this.createdAt = Instant.now();
    }

    @PreUpdate
    public void handleBeforeUpdateBy() {
        this.updatedBy = SecurityUtils.getCurrentUserLogin().isPresent() == true
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        this.updatedAt = Instant.now();
    }

}
