package fr.upjvthomashromain.bibliouniv.configuration;

import fr.upjvthomashromain.bibliouniv.entity.User;
import fr.upjvthomashromain.bibliouniv.repository.UserRepository;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Use Constructor Injection instead of @Autowired on the field
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        String roleName = (user.getRole() != null) ? user.getRole().getRoleName() : "user";
        
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(roleName)
        );

        return new CustomUserDetails(user, authorities);
    }
}