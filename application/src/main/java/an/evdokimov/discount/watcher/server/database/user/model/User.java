package an.evdokimov.discount.watcher.server.database.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "`user`")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_id_generator"
    )
    @SequenceGenerator(
            name = "user_id_generator",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    private Long id;
    @NotNull
    private String login;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private LocalDateTime registerDate;
    @Enumerated(EnumType.STRING)
    @NotNull
    private UserRole role;
    @Builder.Default
    @NotNull
    private boolean enabled = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRole().getAuthorities();
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User that = (User) o;
        return getId() != null
                && Objects.equals(getId(), that.getId())
                && Objects.equals(getLogin(), that.getLogin())
                && Objects.equals(getPassword(), that.getPassword())
                && Objects.equals(getName(), that.getName())
                && Objects.equals(getRegisterDate(), that.getRegisterDate())
                && Objects.equals(getRole(), that.getRole())
                && Objects.equals(isEnabled(), that.isEnabled());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLogin(), getPassword(), getName(), getRegisterDate(), getRole(), isEnabled());
    }
}
