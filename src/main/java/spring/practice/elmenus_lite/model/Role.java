package spring.practice.elmenus_lite.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import spring.practice.elmenus_lite.model.audit.Auditable;

@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "roles")
@NoArgsConstructor
public class Role extends Auditable implements GrantedAuthority {
    @Id
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
