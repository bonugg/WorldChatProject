package com.example.WorldChatProject.user.security.auth;

import com.example.WorldChatProject.user.dto.UserDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetails implements UserDetails{

    private UserDTO userDTO;

    public PrincipalDetails(UserDTO userDTO){
        this.userDTO = userDTO;
    }

    public UserDTO getUser() {
        return userDTO;
    }

    @Override
    public String getPassword() {
        return userDTO.getUserPwd();
    }

    @Override
    public String getUsername() {
        return userDTO.getUserName();
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
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        userDTO.getRoleList().forEach(r -> {
            authorities.add(()->{ return r;});
        });
        return authorities;
    }
}
