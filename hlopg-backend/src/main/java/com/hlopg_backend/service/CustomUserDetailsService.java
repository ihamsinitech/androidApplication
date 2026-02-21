// package com.hlopg_backend.service;

// import java.util.Collections;

// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.core.userdetails.UsernameNotFoundException;
// import org.springframework.stereotype.Service;

// import com.hlopg_backend.model.User;
// import com.hlopg_backend.repository.UserRepository;

// import lombok.RequiredArgsConstructor;

// @Service
// @RequiredArgsConstructor
// public class CustomUserDetailsService implements UserDetailsService {
    
//     private final UserRepository userRepository;
    
//     @Override
//     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//         User user = userRepository.findByEmail(username)
//                 .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));
        
//         return new org.springframework.security.core.userdetails.User(
//                 user.getEmail(),
//                 user.getPassword(),
//                 Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType()))
//         );
//     }
// }
package com.hlopg_backend.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hlopg_backend.model.Owner;
import com.hlopg_backend.model.User;
import com.hlopg_backend.repository.OwnerRepository;
import com.hlopg_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final OwnerRepository ownerRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find in users table
        User user = userRepository.findByEmail(username).orElse(null);
        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getUserType()))
            );
        }
        
        // Then try in owners table
        Owner owner = ownerRepository.findByEmail(username).orElse(null);
        if (owner != null) {
            return new org.springframework.security.core.userdetails.User(
                    owner.getEmail(),
                    owner.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + owner.getUserType()))
            );
        }
        
        throw new UsernameNotFoundException("User not found with email: " + username);
    }
}