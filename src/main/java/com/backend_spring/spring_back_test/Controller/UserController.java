package com.backend_spring.spring_back_test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backend_spring.spring_back_test.Models.User;
import com.backend_spring.spring_back_test.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id).map(user -> {
            user.setName(userDetails.getName());
            user.setLastName(userDetails.getLastName());
            user.setEmail(userDetails.getEmail());
            user.setPassword(userDetails.getPassword());
            user.setEstado(userDetails.getEstado());
            user.setRoles(userDetails.getRoles());
            user.setGender(userDetails.getGender());
            user.setCity(userDetails.getCity());
            user.setAddress(userDetails.getAddress());
            user.setCompany(userDetails.getCompany());
            user.setMobile(userDetails.getMobile());
            user.setTele(userDetails.getTele());
            user.setWebsite(userDetails.getWebsite());
            user.setBirthday(userDetails.getBirthday());
            return userRepository.save(user);
        }).orElseGet(() -> {
            userDetails.setId(id);
            return userRepository.save(userDetails);
        });
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
