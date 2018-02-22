package com.pcalouche.spat.restservices.api.user.controller;

import com.pcalouche.spat.restservices.api.AbstractSpatController;
import com.pcalouche.spat.restservices.api.dto.UserDto;
import com.pcalouche.spat.restservices.api.entity.User;
import com.pcalouche.spat.restservices.api.user.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = UserEndpoints.ROOT)
public class UserController extends AbstractSpatController {
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(UserService userService,
                          ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getUsers()
                .stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{username}")
    public UserDto getByUsername(@PathVariable String username) {
        return modelMapper.map(userService.getByUsername(username), UserDto.class);
    }

    @PostMapping
    public UserDto saveUser(@RequestBody UserDto userDto) {
        User user = modelMapper.map(userDto, User.class);
        return modelMapper.map(userService.saveUser(user), UserDto.class);
    }

    @DeleteMapping(value = "/{id}")
    public boolean deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
