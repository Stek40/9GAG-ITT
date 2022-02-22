package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.*;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.PostServices;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.awt.SystemColor.text;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostServices postServices;
    @Autowired
    private UserController userController;
    @Autowired
    private ModelMapper modelMapper;



   @GetMapping("/aide")
   public ResponseEntity<PostWithoutCommentPostDto> getPostWithComment(){
       Post post = postRepository.getById(2L);
       System.out.println(post.getComments().size() + "--------------------------");
       return ResponseEntity.ok(modelMapper.map(post,PostWithoutCommentPostDto.class));
   }

    @GetMapping("/posts/getById/{id}")
    public Post getPostById(@PathVariable int id){
        return postRepository.getById((long) id);
    }

    @SneakyThrows
    @PostMapping("new_post")
    public ResponseEntity<PostDto> createPost(@RequestParam(name = "file") MultipartFile file,
                                                         @RequestParam(name = "description")String description,
                                                         @RequestParam(name = "categoryId") int categoryId,
                                                         HttpServletRequest request,
                                                         HttpSession session){
        userController.validateLogin(request);

        String nameAndExt = postServices.saveMedia(file);
        long userId = (Long)session.getAttribute(UserController.User_Id);
        Post p = postServices.create(description, nameAndExt, categoryId, userId);
        postRepository.save(p);
        PostDto pDto = modelMapper.map(p, PostDto.class);
        pDto.setUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(pDto);
    }

    @PutMapping("/save_post")
    public ResponseEntity<UserResponseDto> savePost(@RequestParam("postId") int postId , HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        User user = postServices.savedPost(postId,userId);
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }
    @PutMapping("/unsave_post")
    public ResponseEntity<UserResponseDto> unSave(@RequestParam("postId") int postId , HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = userRepository.getIdByRequest(request);
        User user = postServices.unSavedPost(postId, (Long) request.getSession().getAttribute(UserController.User_Id));
        return ResponseEntity.ok(modelMapper.map(user,UserResponseDto.class));
    }

    @PutMapping("/posts/{id}/upvote")
    public ResponseEntity<PostDto> upvotePost(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        long userId = (Long)session.getAttribute(UserController.User_Id);
        Post p = postServices.votePost(true, id, userId);
        postRepository.save(p);

        PostDto dto = modelMapper.map(p, PostDto.class);
        return ResponseEntity.ok(dto);
    }
    @PutMapping("/posts/{id}/downvote")
    public ResponseEntity<PostDto> downvotePost(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        long userId = (Long)session.getAttribute(UserController.User_Id);
        Post p = postServices.votePost(false, id, userId);
        postRepository.save(p);

        PostDto dto = modelMapper.map(p, PostDto.class);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/posts")
    public ResponseEntity<List<PostWithoutOwnerDto>> getAllPosts(@RequestParam("sort by upvotes") boolean isByUpvotes) {
        //no login
        List<Post> allPosts;
        if(isByUpvotes) {
            allPosts = postRepository.getAllOrderByUpvotes();
        } else {
            allPosts = postRepository.getAllOrderByUploadDate();
        }
        List<PostWithoutOwnerDto> postDtos = new ArrayList<>();
        for (Post p : allPosts) {
            postDtos.add(modelMapper.map(p, PostWithoutOwnerDto.class));
        }
        return ResponseEntity.ok().body(postDtos);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostWithCategoryDto> getPostById(@PathVariable long id) {
        //no login
        Post p = postServices.getPostById(id);
        PostWithCategoryDto pDto = postServices.PostToDtoConversion1(p);
        return ResponseEntity.ok().body(pDto);
    }
    @GetMapping("/users/upvoted")
    public ResponseEntity<List<PostWithoutOwnerDto>> getUpvotedPosts(HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        long userId = (Long)session.getAttribute(UserController.User_Id);//todo method with Marto

        Set<Post> posts = userRepository.getById(userId).getUpvotedPosts();

        List<PostWithoutOwnerDto> postsDto = postServices.sortPostsByDate(new ArrayList<>(posts)); //by date is not correct
        return ResponseEntity.ok().body(postsDto);
    }
    @GetMapping("/post/allSavedPosts")
    public ResponseEntity<UserWithAllSavedPostDto> getAllSavedPost(HttpServletRequest httpServletRequest){
        ValidateData.validatorLogin(httpServletRequest);

        User user = userRepository.getById((Long) httpServletRequest.getSession().getAttribute(UserController.User_Id));
        return ResponseEntity.ok(modelMapper.map(user,UserWithAllSavedPostDto.class));
    }
    @GetMapping("/users/comments")
    public ResponseEntity<List<Post>> getCommentedPosts(@RequestParam("id") long id, HttpServletRequest request) {
        userController.validateLogin(request);
        return null;
    }
    @DeleteMapping("/posts/{id}/delete")
    public void deletePost(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        postServices.getPostById(id);
        Post p = postRepository.getById(id);
        if(p.getOwner().getId() == (Long)session.getAttribute(UserController.User_Id)) {//if current user is owner
            postRepository.deleteById(id);
        }
        else {
            throw new UnauthorizedException("Only the owner of the post can delete it!");
        }
    }
    @GetMapping("/posts/search/{search}")
    public ResponseEntity<List<PostWithoutOwnerDto>> searchPosts(@PathVariable String search) {
        //serialize "search string" into keywords
        //search in the descriptions of the posts for each word
        //return posts sorted by most common keywords found first

        List<PostWithoutOwnerDto> result = postServices.searchPostGenerator(search);

       return ResponseEntity.ok().body(result);
    }
}
