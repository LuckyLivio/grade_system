package com.grade.service;

import com.grade.entity.User;
import com.grade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        // 添加空值检查
        if (user.getRole() == null) {
            throw new UsernameNotFoundException("用户角色未设置: " + username);
        }
        
        if (user.getEnabled() == null) {
            user.setEnabled(true); // 设置默认值
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        // 修复：直接使用枚举的name()方法
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(!user.getEnabled())
            .credentialsExpired(false)
            .disabled(!user.getEnabled())
            .build();
    }
}