package co.kurrant.app.public_api.model;

import co.dalicious.domain.user.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SecurityUser implements UserDetails {

  private static final long serialVersionUID = -4493193809465704737L;

  private BigInteger id;
  private Role role;
  private String email;

  private String name;
  private String password;

//  @Builder.Default
//  private List<String> roles = new ArrayList<>();

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
//    return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
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
    return this.email;
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
