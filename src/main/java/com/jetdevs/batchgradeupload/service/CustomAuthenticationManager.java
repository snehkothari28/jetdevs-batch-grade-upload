package com.jetdevs.batchgradeupload.service;

import com.jetdevs.batchgradeupload.entity.Role;
import com.jetdevs.batchgradeupload.entity.User;
import com.jetdevs.batchgradeupload.repository.RoleRepository;
import com.jetdevs.batchgradeupload.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomAuthenticationManager implements AuthenticationManager {
    Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);
    // Inject your UserRepository or any data source to retrieve user data
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder encoder;

    /**
     * Get the username and password from authentication object and validate with password encoders matching method
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userRepository.findByName(username).orElse(null);
        if (user == null) {
            throw new BadCredentialsException("Details not found");
        }
        if (encoder.matches(password, user.getHashedPassword())) {
            Role role = user.getRole();
            List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
            grantedAuthorityList.add(new SimpleGrantedAuthority(role.getName()));
            logger.info("Successfully Authenticated the user");
            return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorityList);
        } else {
            throw new BadCredentialsException("Password mismatch");
        }
    }


}
