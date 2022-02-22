package com.example.springproject.controller;

import com.example.springproject.ValidateData;
import com.example.springproject.dto.*;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import com.example.springproject.services.PostServices;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Type;
import java.util.*;

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


   @GetMapping("/martin/allPosts")
   public ResponseEntity<PostWithoutCommentPostDto>   getlAllPostByDate(){

       ArrayList<Post> posts = (ArrayList<Post>) postRepository.findAll();
       Comparator<Post> comparatorDate = Comparator.comparing(Post::getUploadDate);
       posts.sort(comparatorDate);
      ArrayList<PostWithoutCommentPostDto> allPosts =modelMapper.map(posts, (Type) PostWithoutCommentPostDto.class);
     return ResponseEntity.ok(modelMapper.map(posts,PostWithoutCommentPostDto.class));

   }
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

    @PostMapping("/new_post")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto pDto, HttpSession session, HttpServletRequest request) {
        userController.validateLogin(request);
        long userId = (Long)session.getAttribute(UserController.User_Id);
        System.out.println(userId);
        Post p = postServices.create(pDto.getDescription(), pDto.getMediaUrl(), pDto.getCategoryId(), userId);
        postRepository.save(p);
        pDto = modelMapper.map(p, PostDto.class);
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

    @PutMapping(value = {"/posts/{id}/upvote", "/posts/{id}/downvote"})
    public ResponseEntity<PostDto> votePost(@PathVariable long id, HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);

        long userId = (Long)session.getAttribute(UserController.User_Id);

        Post p = postServices.votePost(request.getRequestURI().contains("up"), id, userId);

        postRepository.save(p);

        PostDto dto = modelMapper.map(p, PostDto.class);
        return ResponseEntity.ok(dto);
    }
    @GetMapping("/all_posts")
    public ResponseEntity<List<PostWithoutOwnerDto>> getAllPosts() {
        //no need to be logged
        List<Post> list = postRepository.findAll();
        List<PostWithoutOwnerDto> lst = new ArrayList<>();
        for (Post p : list) {
            PostWithoutOwnerDto postWithoutOwnerDto = modelMapper.map(p,PostWithoutOwnerDto.class);
            postWithoutOwnerDto.setCategoryName(p.getCategory().getName());
            lst.add(postWithoutOwnerDto);
        }
        return ResponseEntity.ok(lst);
    }
    @GetMapping("/posts/{id}")
    public ResponseEntity<PostWithOwnerAndCategoryDto> getPostById(@PathVariable long id) {
        //no need to be logged
        Post p = postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist"));
        PostWithOwnerAndCategoryDto pDto = modelMapper.map(p, PostWithOwnerAndCategoryDto.class);
        pDto.setUserId(p.getOwner().getId());
        pDto.setOwner(modelMapper.map(p.getOwner(), UserWithoutPostsDto.class));
        pDto.setCategory(modelMapper.map(p.getCategory(), CategoryWithoutPostsDto.class));
        return ResponseEntity.ok().body(pDto);
        //return ResponseEntity.ok(postRepository.findById(id).orElseThrow(() -> new NotFoundException("post with id=" + id + " doesn't exist")));
    }
    @GetMapping("/posts/{id}/download")
    public String downloadPostMedia(@PathVariable long id, HttpServletRequest request) {
        userController.validateLogin(request);
        if(!postRepository.existsById(id)) {
            throw new NotFoundException("post with id=" + id + " doesn't exist");
        }
        return postRepository.findById(id).get().getMediaUrl();
    }
    @GetMapping("/users/upvoted")
    public ResponseEntity<Set<PostWithoutOwnerDto>> getUpvotedPosts(HttpServletRequest request, HttpSession session) {
        userController.validateLogin(request);
        long userId = (Long)session.getAttribute(UserController.User_Id);

        Set<Post> posts = userRepository.getById(userId).getUpvotedPosts();
        Set<PostWithoutOwnerDto> psts = new HashSet<>();
        for (Post p : posts) {
            psts.add(modelMapper.map(p, PostWithoutOwnerDto.class));
        }
        return ResponseEntity.ok().body(psts);
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
}
