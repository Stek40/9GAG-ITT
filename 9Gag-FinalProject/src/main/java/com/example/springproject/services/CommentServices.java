package com.example.springproject.services;

import com.example.springproject.dto.CommentWithoutOwnerDto;
import com.example.springproject.dto.PostWithoutCommentPostDto;
import com.example.springproject.dto.newDtos.comment.AllCommentsOnPostDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class CommentServices {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ModelMapper modelMapper;


    public Comment createComment(MultipartFile file, String text, long postId, long userId) {

        if (text == null && file == null) {
            throw new BadRequestException("Please enter a comment");
        }
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> commentOwner = userRepository.findById(userId);
        if (post.isPresent()) {
            Comment comment = new Comment();
            if (!file.isEmpty()) {
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String name = System.nanoTime() + "." + ext;
                try {
                    Files.copy(file.getInputStream(), new File("commentImages" + File.separator + name).toPath());
                    comment.setMediaUrl(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (text != null) {
                comment.setText(text);
            }

            comment.setUpvotes(0);
            comment.setDownvotes(0);
            comment.setCommentOwner(commentOwner.get());
            comment.setDateTime(LocalDateTime.now());
            comment.setPost(post.get());
            commentRepository.save(comment);
            post.get().getComments().add(comment);

            return comment;

        }
        throw new NotFoundException("Post not found !");

    }

    public Comment upVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Comment comment = commentRepository.getById(commentId);
        if (comment.getDownVoters().contains(user)) {
            comment.getDownVoters().remove(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
        }
        if (!comment.getUppVoters().contains(user)) {
            comment.getUppVoters().add(user);
            comment.setUpvotes(comment.getUppVoters().size());
            commentRepository.save(comment);
            user.getUpVoteComments().add(comment);
            return comment;
        }

        throw new BadRequestException("The user already upvote this comment !");
    }

    public Comment dowVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            if (comment.get().getUppVoters().contains(user)) {
                comment.get().getUppVoters().remove(user);
                comment.get().setUpvotes(comment.get().getUppVoters().size());
                user.getUpVoteComments().remove(comment.get());
                commentRepository.save(comment.get());
                userRepository.save(user);
            }
            if (!comment.get().getDownVoters().contains(user)) {
                user.getDownVote().add(comment.get());
                comment.get().getDownVoters().add(user);
                comment.get().setDownvotes(comment.get().getDownVoters().size());
                commentRepository.save(comment.get());
                userRepository.save(user);
                return comment.get();

            }
            throw new BadRequestException("The user already downvote this comment !");
        }
        throw new NotFoundException("Comment not found !");
    }

    public Comment removeVot(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            throw new NotFoundException("Comment not found !");
        }
        if (comment.get().getDownVoters().contains(user)) {
            comment.get().getDownVoters().remove(user);
            comment.get().setDownvotes(comment.get().getDownVoters().size());
            user.getDownVote().remove(comment);
            userRepository.save(user);
            commentRepository.save(comment.get());
            return comment.get();
        }
        if (comment.get().getUppVoters().contains(user)) {
            comment.get().getUppVoters().remove(user);
            comment.get().setUpvotes(comment.get().getUppVoters().size());
            user.getUpVoteComments().remove(comment);
            userRepository.save(user);
            commentRepository.save(comment.get());
            return comment.get();
        }
        throw new UnauthorizedException("Тhe user didn't  vote !");
    }

    public Set<PostWithoutCommentPostDto> getAllCommentPosts(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Set<PostWithoutCommentPostDto> allCommentedPosts = new TreeSet<>();
        for (Comment c : user.getComments()) {
            PostWithoutCommentPostDto postWithoutCommentPostDto = modelMapper.map(c.getPost(), PostWithoutCommentPostDto.class);
            allCommentedPosts.add(postWithoutCommentPostDto);
        }
        return allCommentedPosts;
    }

    public AllCommentsOnPostDto getAllCommentByPostId(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            AllCommentsOnPostDto allComments = modelMapper.map(post.get(), AllCommentsOnPostDto.class);
            allComments.setComments(allComments.getComments().stream().sorted((c1, c2) -> (int) (c2.getUpvotes() - c1.getUpvotes())).collect(Collectors.toList()));
            return allComments;
        }
        throw new NotFoundException("Post not found !");

    }

    public AllCommentsOnPostDto getAllCommentByPostDate(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            AllCommentsOnPostDto allComments = modelMapper.map(post.get(), AllCommentsOnPostDto.class);
            allComments.setComments(allComments.getComments().stream().sorted((c1, c2) -> (c2.getDateTime().compareTo(c1.getDateTime()))).collect(Collectors.toList()));

            return allComments;
        }
        throw new NotFoundException("Post not found !");
    }
//    public Comment removeComment(long commendId, HttpServletRequest request){
//        //TODO Wait Stefan posts_have_comments
//        User user = userRepository.getUserByRequest(request);
//        Comment comment = commentRepository.getById(commendId);
//        if (comment.getCommentOwner() != user && comment.getPost().getOwner() != user){
//            throw new UnauthorizedException("The user is not the owner of the comment!");
//        }
//        if (comment.getCommentOwner() == user){
//            commentRepository.delete(comment);
//            user.getComments().remove(comment);
//            comment.getPost().getComments().remove(comment);
//        }
//    }

}
