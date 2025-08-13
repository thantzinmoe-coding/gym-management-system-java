package org._java_proj.gym_management_system.features.users.service.impl;

import lombok.RequiredArgsConstructor;
import org._java_proj.gym_management_system.features.users.repository.UserRepository;
import org._java_proj.gym_management_system.model.User;
import org._java_proj.gym_management_system.model.UserDetail;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public  class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;



//    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email);
//        return new UserDetail(user);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        return new UserDetail(user);
    }
}
