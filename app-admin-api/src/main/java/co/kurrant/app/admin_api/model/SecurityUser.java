package co.kurrant.app.admin_api.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import co.dalicious.domain.user.entity.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements  UserDetails {

  private static final long serialVersionUID = -4493193809465704737L;

  private String username;
  private String password;
  private Role role;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(this.role.getAuthority());
    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(simpleGrantedAuthority);

    return collection;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public String getPassword() {
    return this.password;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public String getUsername() {
    return this.username;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @Override
  public boolean isEnabled() {
    return true;
  }

}
