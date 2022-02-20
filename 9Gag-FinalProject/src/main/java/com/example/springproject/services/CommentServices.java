package com.example.springproject.services;

import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.model.Comment;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CommentServices {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;



    public Comment createComment(MultipartFile file, String text, long postId, long userId) {
        if (text == null && file == null){
            throw new BadRequestException("Please enter a comment");
        }
        Optional<Post> post = postRepository.findById(postId);
        Optional<User> commentOwner = userRepository.findById(userId);
        if (post.isPresent()){
            Comment comment = new Comment();
            if (file != null){
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                String name = System.nanoTime() +"."+ ext;
                try {
                    Files.copy(file.getInputStream(), new File("commentImages" + File.separator + name).toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                comment.setMediaUrl(name);
            }
            if (text != null){
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
        if (comment.getDownVoters().contains(user)){
            comment.getDownVoters().remove(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
        }
      if (!comment.getUppVoters().contains(user)){
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
        Comment comment = commentRepository.getById(commentId);
        if (comment.getUppVoters().contains(user)) {
            comment.getUppVoters().remove(user);
            comment.setUpvotes(comment.getUppVoters().size());
            user.getUpVoteComments().remove(comment);
            commentRepository.save(comment);
        }
        if (!comment.getDownVoters().contains(user)) {

            comment.getDownVoters().add(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
            return comment;

        }

        throw new BadRequestException("The user already downvote this comment !");
    }

}
