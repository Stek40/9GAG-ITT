package com.example.springproject.services;

import com.example.springproject.dto.UserEditDto;
import com.example.springproject.dto.UserLoginDto;
import com.example.springproject.dto.UserResponseDto;
import com.example.springproject.dto.newDtos.user.UserCreatedPostsByDate;
import com.example.springproject.dto.newDtos.user.UserRegisterDto;
import com.example.springproject.dto.newDtos.user.UserResponseDtoRegister;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.DateTimeParseException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Category;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.CountryRepository;
import com.example.springproject.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    public static final String LOGGED = "logged";
    public static final String LOGGED_FROM = "loggedFrom";
    public static final String User_Id = "user_id";

    String regexUsername = "^(?=.{6,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
    String regexPassword = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$";
    String regexEmail = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    public ResponseEntity<UserResponseDtoRegister> register(UserRegisterDto u) {
        String username = u.getUsername();
        String password = u.getPassword();
        String confirmPassword = u.getConfirmPassword();
        String full_name = u.getFull_name();
        String about = u.getAbout();
        String email = u.getEmail();
        boolean show_sensitive_content = u.isShow_sensitive_content();
        String gender = u.getGender();
        if (username == null) {
            throw new BadRequestException("Username is mandatory !");
        }
        if (email == null) {
            throw new BadRequestException("Email is mandatory !");
        }
        if (full_name == null) {
            throw new BadRequestException("Full name is mandatory !");
        }
        if (password == null) {
            throw new BadRequestException("Password is mandatory !");
        }
        if (u.getCountry_id() == 0) {
            throw new BadRequestException("Country is mandatory !");
        }
        if (confirmPassword == null) {
            throw new BadRequestException("Confirm password is mandatory !");
        }
        if (!username.matches(regexUsername)) {
            throw new BadRequestException("Invalid username");
        }
        if (!password.matches(regexPassword)) {
            throw new BadRequestException("Invalid password");
        }

        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("The password miss match !");
        }

        if (full_name.isBlank() || full_name.length() <= 4) {
            throw new BadRequestException("Invalid full name !");
        }
        validateEmail(email);
        if (userRepository.findUserByUsername(username) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (userRepository.findUserByEmail(email) != null) {
            throw new BadRequestException("User already exists !");
        }
        if (!countryRepository.findById(u.getCountry_id()).isPresent()) {
            throw new BadRequestException("Invalid country !");
        }
        User user = new User();
        if (gender != null && gender.matches("[F]|[M]|[O]")) {
            user.setGender(gender);
        }
        if (about != null && about.length() > 3) {
            user.setAbout(about);
        }
        if (u.getDate_of_birth() != null && !u.getDate_of_birth().isBlank()) {
            try {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy");
                LocalDate localDate = LocalDate.parse(u.getDate_of_birth(), dateTimeFormatter);
                user.setDate_of_birth(localDate);
            } catch (Exception e) {
                throw new DateTimeParseException("Invalid date of birth");
            }
        }

        user.setHidden(u.isHidden());
        user.setShow_sensitive_content(show_sensitive_content);
        user.setCountry_id(u.getCountry_id());
        user.setEmail(email);
        user.setFull_name(full_name);
        user.setPassword(passwordEncoder.encode(password));
        user.setUsername(username);
        userRepository.save(user);
        return ResponseEntity.ok(modelMapper.map(user, UserResponseDtoRegister.class));
    }

    public ResponseEntity<UserResponseDto> getById(long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            return ResponseEntity.ok(modelMapper.map(opt.get(),UserResponseDto.class));
        }
        throw new NotFoundException("User not found");
    }

    public ResponseEntity<UserResponseDto> logIn(UserLoginDto userDto,HttpServletRequest request) {
        User user = userRepository.findUserByUsername(userDto.getUsername());
        if (user == null) {
            throw new BadRequestException("Incorrect username or password!");
        }
        if (userDto.getUsername() == null || userDto.getUsername().isBlank()) {
            throw new BadRequestException("Username is mandatory !");
        }
        if (userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            throw new BadRequestException("Password is mandatory !");
        }
        if (!passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect username or password !");
        }
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(LOGGED_FROM, request.getRemoteAddr());
        session.setAttribute(User_Id, user.getId());
        UserResponseDto userResponseDto = modelMapper.map(user, UserResponseDto.class);
        return ResponseEntity.ok(userResponseDto);
    }

    public User changeProfilePicture(MultipartFile multipartFile, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        String ext = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        String name = String.valueOf(System.nanoTime()) + "." + ext;
        try {
            Files.copy(multipartFile.getInputStream(), new File("uploads" + File.separator + name).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setProfile_picture_url(name);
        userRepository.save(user);
        return user;
    }

    public User changeEmail(UserEditDto editDto, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        if (!passwordEncoder.matches(editDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }
        validateEmail(editDto.getNewEmail());
        if (userRepository.findUserByEmail(editDto.getNewEmail()) != null) {
            throw new BadRequestException("Email already exist !");
        }
        validateEmail(editDto.getNewEmail());
        user.setEmail(editDto.getNewEmail());
        userRepository.save(user);
        return user;
    }

    public User changePassword(UserEditDto dto, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password !");
        }
        if (!dto.getNewPassword().matches(regexPassword)) {
            throw new BadRequestException("Invalid new password !");
        }
        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new BadRequestException("The passwords miss match !");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
        return user;
    }

    public User changeUsername(UserEditDto dto, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        if (userRepository.findUserByUsername(dto.getNew_username()) != null) {
            throw new BadRequestException("Username already exist !");
        }
        user.setUsername(dto.getNew_username());
        userRepository.save(user);
        return user;
    }

    public User setSensitiveContentTrue(UserEditDto dto, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        user.setShow_sensitive_content(true);
        userRepository.save(user);
        return user;
    }

    public User setIsHidden(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);

        user.setHidden(true);
        userRepository.save(user);
        return user;
    }

    public User setIsPublic(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        user.setHidden(false);
        userRepository.save(user);
        return user;
    }

    public User deleteUser(UserEditDto editDto, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        if (!passwordEncoder.matches(editDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid password !");
        }
        userRepository.delete(user);
        return user;
    }

    private boolean validateEmail(String email) {

        if (!email.matches(regexEmail)) {
            throw new BadRequestException("Invalid email !");
        }
        return true;
    }


    public ResponseEntity<UserCreatedPostsByDate> getAllCreatedPosts(HttpServletRequest request) {
        User user = userRepository.getById(userRepository.getIdByRequest(request));
        UserCreatedPostsByDate userAllPosts = modelMapper.map(user,UserCreatedPostsByDate.class);
        userAllPosts.setPosts(userAllPosts.getPosts().
                stream().sorted((p1,p2)->p2.getUploadDate().compareTo(p1.getUploadDate())).collect(Collectors.toList()));
        return ResponseEntity.ok(userAllPosts);
    }
    public ResponseEntity<UserCreatedPostsByDate> getAllCreatedPostsByVote(HttpServletRequest request) {
        User user = userRepository.getById(userRepository.getIdByRequest(request));
        UserCreatedPostsByDate userAllPosts = modelMapper.map(user,UserCreatedPostsByDate.class);
        userAllPosts.setPosts(userAllPosts.getPosts().
                stream().sorted((p1,p2)->p2.getUpvotes()-(p1.getUpvotes())).collect(Collectors.toList()));
        return ResponseEntity.ok(userAllPosts);
    }

    public ResponseEntity<UserCreatedPostsByDate> getUserByIdWithAllPosts(long userId) {
        User user = userRepository.getById(userId);

        UserCreatedPostsByDate userAllPosts = modelMapper.map(user,UserCreatedPostsByDate.class);
        userAllPosts.setPosts(userAllPosts.getPosts().
                stream().sorted((p1,p2)->p2.getUploadDate().compareTo(p1.getUploadDate())).collect(Collectors.toList()));
        return ResponseEntity.ok(userAllPosts);
    }


    public ResponseEntity<UserResponseDto> addCategory(long cId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Category> category = categoryRepository.findById(cId);
        if (category.isPresent()){
            if (user.getCategories().contains(category.get())){
                throw new BadRequestException("Category already exist !");
            }
        user.getCategories().add(categoryRepository.getById(cId));
        userRepository.save(user);
        category.get().getUsers().add(user);
        categoryRepository.save(category.get());
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
        throw new NotFoundException("Category not found !");
    }

    public ResponseEntity<UserResponseDto> removeCategory(long cId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Category> category = categoryRepository.findById(cId);
        if (category.isPresent()){
            if (!user.getCategories().contains(category.get())){
                throw new BadRequestException("Category is not in the favorites !");
            }
            user.getCategories().remove(category.get());
            userRepository.save(user);
            category.get().getUsers().remove(user);
            categoryRepository.save(category.get());
            return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
        }
        throw new NotFoundException("Category not found !");
    }
}
