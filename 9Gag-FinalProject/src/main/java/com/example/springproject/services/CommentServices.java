package com.example.springproject.services;

import com.example.springproject.dto.CommentResponseDto;
import com.example.springproject.dto.CommentWithoutOwnerDto;
import com.example.springproject.dto.PostWithoutCommentPostDto;
import com.example.springproject.dto.UserWithCommentsDto;
import com.example.springproject.dto.newDtos.comment.AllCommentsOnPostDto;
import com.example.springproject.dto.newDtos.postDtos.DisplayPostDto;
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
import java.util.ArrayList;
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

    public ResponseEntity<CommentResponseDto> createComment(MultipartFile file, String text, long postId, HttpServletRequest request) {
        if (text == null && file == null) {
            throw new BadRequestException("Please enter a comment");
        }
        long userId = userRepository.getIdByRequest(request);
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
            comment.setCommentOwner(commentOwner.get());
            comment.setDateTime(LocalDateTime.now());
            comment.setPost(post.get());
            commentRepository.save(comment);
            CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
            commentResponseDto.setUserId(userId);
            return ResponseEntity.ok(commentResponseDto);
        }
        throw new NotFoundException("Post not found !");

    }

    public ResponseEntity<CommentResponseDto> upVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()){
            throw new NotFoundException("Comment not found !");
        }
        if (comment.get().getDownVoters().contains(user)) {
            comment.get().getDownVoters().remove(user);
            comment.get().setDownvotes(comment.get().getDownVoters().size());
            commentRepository.save(comment.get());
        }
        if (!comment.get().getUppVoters().contains(user)) {
            comment.get().getUppVoters().add(user);

            CommentResponseDto commentResponseDto = modelMapper.map(comment.get(),CommentResponseDto.class);
            commentResponseDto.setUpvotes(comment.get().getUppVoters().size());
            commentRepository.save(comment.get());
            return ResponseEntity.ok(commentResponseDto);
        }


        throw new BadRequestException("The user already upvote this comment !");
    }

    public ResponseEntity<CommentResponseDto> dowVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isPresent()) {
            if (comment.get().getUppVoters().contains(user)) {
                comment.get().getUppVoters().remove(user);
                comment.get().setUpvotes(comment.get().getUppVoters().size());
                commentRepository.save(comment.get());
            }
            if (!comment.get().getDownVoters().contains(user)) {
                user.getDownVote().add(comment.get());
                comment.get().getDownVoters().add(user);
                comment.get().setDownvotes(comment.get().getDownVoters().size());
                commentRepository.save(comment.get());
                return ResponseEntity.ok(modelMapper.map(comment.get(), CommentResponseDto.class));
            }
            throw new BadRequestException("The user already downvote this comment !");
        }
        throw new NotFoundException("Comment not found !");
    }

    public ResponseEntity<CommentWithoutOwnerDto> removeVot(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (!comment.isPresent()) {
            throw new NotFoundException("Comment not found !");
        }
        if (comment.get().getDownVoters().contains(user)) {
            comment.get().getDownVoters().remove(user);
            comment.get().setDownvotes(comment.get().getDownVoters().size());
            commentRepository.save(comment.get());
            return ResponseEntity.ok(modelMapper.map(comment.get(),CommentWithoutOwnerDto.class));
        }
        if (comment.get().getUppVoters().contains(user)) {
            comment.get().getUppVoters().remove(user);
            comment.get().setUpvotes(comment.get().getUppVoters().size());
            user.getUpVoteComments().remove(comment.get());
            userRepository.save(user);
            commentRepository.save(comment.get());
            return ResponseEntity.ok(modelMapper.map(comment.get(),CommentWithoutOwnerDto.class));
        }
        throw new UnauthorizedException("Тhe user didn't vote !");
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
//    public Set<DisplayPostDto> getAllCommentPosts(HttpServletRequest request) {
//        User user = userRepository.getUserByRequest(request);
//        Set<DisplayPostDto> allCommentedPosts = new TreeSet<>((p1, p2) -> {
//            return p2.getUploadDate().compareTo(p1.getUploadDate());
//        });
//        for (Comment c : user.getComments()) {
//            allCommentedPosts.add(postServices.PostToDisplayPostDtoConversion(c.getPost()));
//        }
//        return allCommentedPosts;
//    }

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
            allComments.setComments(allComments.getComments().stream()
                    .sorted((c1, c2) -> (c2.getDateTime().compareTo(c1.getDateTime()))).collect(Collectors.toList()));

            return allComments;
        }
        throw new NotFoundException("Post not found !");
    }
    public ResponseEntity<CommentResponseDto> removeComment(long commendId, HttpServletRequest request){

        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commendId);
        if (comment.isPresent()) {
            if (comment.get().getCommentOwner() != user && comment.get().getPost().getOwner() != user) {
                throw new UnauthorizedException("The user is not the owner of the comment!");
            }
                commentRepository.delete(comment.get());
            return ResponseEntity.ok(modelMapper.map(comment.get(),CommentResponseDto.class));
        }
        throw new NotFoundException("Comment not found !");
    }

    public ResponseEntity<UserWithCommentsDto> getAllCommentsUser(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        UserWithCommentsDto userWithCommentsDto = modelMapper.map(user,UserWithCommentsDto.class);
        userWithCommentsDto.setComments(userWithCommentsDto.getComments().stream()
                .sorted((c1, c2)->(c2.getDateTime().compareTo(c1.getDateTime()))).collect(Collectors.toList()));
                //allComments.setComments(allComments.getComments().stream()
                //           .sorted((c1, c2) -> (c2.getDateTime().compareTo(c1.getDateTime()))).collect(Collectors.toList()));
        return ResponseEntity.ok(userWithCommentsDto);
    }
}
