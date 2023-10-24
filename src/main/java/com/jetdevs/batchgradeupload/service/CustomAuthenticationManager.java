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

/**
 * Custom Authentication Manager responsible for authenticating users during login.
 */
@Service
public class CustomAuthenticationManager implements AuthenticationManager {

    private final Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * Authenticates the user based on the provided username and password.
     *
     * @param authentication The authentication object containing user credentials.
     * @return Authenticated user object with authorities if successful.
     * @throws AuthenticationException Thrown if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Retrieve user data from the repository based on the provided username.
        User user = userRepository.findByName(username).orElse(null);

        // If user is not found, throw BadCredentialsException.
        if (user == null) {
            throw new BadCredentialsException("Details not found");
        }

        // Validate the provided password with the hashed password stored in the database.
        if (encoder.matches(password, user.getHashedPassword())) {
            Role role = user.getRole();

            // Create a list of GrantedAuthorities with the user's role.
            List<GrantedAuthority> grantedAuthorityList = new ArrayList<>();
            grantedAuthorityList.add(new SimpleGrantedAuthority(role.getName()));

            logger.info("Successfully Authenticated the user");

            // Return an authenticated user object with authorities.
            return new UsernamePasswordAuthenticationToken(username, password, grantedAuthorityList);
        } else {
            // If passwords do not match, throw BadCredentialsException.
            throw new BadCredentialsException("Password mismatch");
        }
    }
}
