package com.metuncc.mlm.security;

import com.metuncc.mlm.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class JwtUserDetails implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    private JwtUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static JwtUserDetails create(User user){
        List<GrantedAuthority> authorityList = new ArrayList<>();
        switch (user.getRole()){
            case USER:
                authorityList.add(new SimpleGrantedAuthority("user"));
                break;
            case LIB:
                authorityList.add(new SimpleGrantedAuthority("lib"));
                break;
            default:
                break;
        }
        return new JwtUserDetails(user.getId(), user.getUsername(), user.getPassword(), authorityList);
    }


    //Not implemented for now
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //Not implemented for now
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //Not implemented for now
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //Not implemented for now
    @Override
    public boolean isEnabled() {
        return true;
    }
}
