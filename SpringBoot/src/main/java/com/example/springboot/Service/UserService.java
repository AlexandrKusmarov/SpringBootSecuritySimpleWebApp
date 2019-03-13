package com.example.springboot.Service;

import com.example.springboot.domain.Role;
import com.example.springboot.domain.User;
import com.example.springboot.repositories.UserCrudRepository;
import com.example.springboot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MailSender mailSender;

    @Autowired
    private UserCrudRepository userCrudRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }
    public boolean addUser(User user){
        User userFromDb = userRepository.findByUsername(user.getUsername());

        if(userFromDb != null){
            return false;
        }

        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());

        userRepository.save(user);

        if(!StringUtils.isEmpty(user.getEmail())){
            String message = String.format(
                    "Hello, %s! \n" +
                            "Welcome to SpringBootApp. Please, visit next link: http://localhost:8082/activate/%s",
                    user.getUsername(),
                    user.getActivationCode()
            );

            mailSender.send(user.getEmail(), "Activation code", message);
        }

        return true;
    }

    public boolean activateUser(String code) {

        User user = userRepository.findByActivationCode(code);

        if(user == null){
            return false;
        }

        user.setActivationCode(null);
        userRepository.save(user);

        return true;
    }

    public void delete(long id){
        userCrudRepository.deleteById(id);
    }
}
