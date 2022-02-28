package com.example.springproject.services;

import com.example.springproject.dto.commentDtos.CommentResponseDto;

import com.example.springproject.dto.userDtos.UserWithCommentsDto;
import com.example.springproject.dto.commentDtos.AllCommentsOnPostDto;
import com.example.springproject.dto.postDtos.DisplayPostDto;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
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
    @Autowired
    private PostServices postServices;
    @Autowired
    private FileServices fileServices;
    private static final int COMMENT_MEDIA_MAX_SIZE = 1024 * 1024 * 5; //5 MB

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CommentResponseDto> createComment(MultipartFile file, String text, long postId, HttpServletRequest request) {

        if (text == null && file == null) {
            throw new BadRequestException("Please enter a comment");
        }
        if (file.getSize() > COMMENT_MEDIA_MAX_SIZE) {
            throw new UnauthorizedException("File is too large. Limit is " + COMMENT_MEDIA_MAX_SIZE / (1024*1024) + " mb");
        }
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found"));
        User commentOwner = userRepository.getUserByRequest(request);
        Comment comment = new Comment();
        if (!file.isEmpty()) {
            fileServices.validateMediaType(file, true);
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            String name = System.nanoTime() + "." + ext;
            try {
                Files.copy(file.getInputStream(), new File("media" + File.separator + "commentImages" + File.separator + name).toPath());
                comment.setMediaUrl(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (text != null) {
            comment.setText(text);
        }
        comment.setCommentOwner(commentOwner);
        comment.setDateTime(LocalDateTime.now());
        comment.setPost(post);
        commentRepository.save(comment);
        CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
        commentResponseDto.setUserId(commentOwner.getId());
        return ResponseEntity.ok(commentResponseDto);
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CommentResponseDto> upVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found !"));

        if (comment.getDownVoters().contains(user)) {
            comment.getDownVoters().remove(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
        }
        if (!comment.getUppVoters().contains(user)) {
            comment.getUppVoters().add(user);

            CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
            commentResponseDto.setUpvotes(comment.getUppVoters().size());
            commentResponseDto.setUserId(user.getId());
            comment.setUpvotes(comment.getUppVoters().size());
            commentRepository.save(comment);
            return ResponseEntity.ok(commentResponseDto);
        }
        throw new BadRequestException("The user already upvote this comment !");
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CommentResponseDto> downVoteComment(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found !"));

        if (comment.getUppVoters().contains(user)) {
            comment.getUppVoters().remove(user);
            comment.setUpvotes(comment.getUppVoters().size());
            commentRepository.save(comment);
        }
        if (!comment.getDownVoters().contains(user)) {
            user.getDownVote().add(comment);
            comment.getDownVoters().add(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
            CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
            commentResponseDto.setUserId(user.getId());
            return ResponseEntity.ok(commentResponseDto);
        }
        throw new BadRequestException("The user already downvote this comment !");
    }

    @Transactional(rollbackFor = SQLException.class)
    public ResponseEntity<CommentResponseDto> removeVote(long commentId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new NotFoundException("Comment not found !"));

        if (comment.getDownVoters().contains(user)) {
            comment.getDownVoters().remove(user);
            comment.setDownvotes(comment.getDownVoters().size());
            commentRepository.save(comment);
            CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
            commentResponseDto.setUserId(user.getId());
            return ResponseEntity.ok(commentResponseDto);
        }
        if (comment.getUppVoters().contains(user)) {
            comment.getUppVoters().remove(user);
            comment.setUpvotes(comment.getUppVoters().size());
            commentRepository.save(comment);
            CommentResponseDto commentResponseDto = modelMapper.map(comment, CommentResponseDto.class);
            commentResponseDto.setUserId(user.getId());
            return ResponseEntity.ok(commentResponseDto);
        }
        throw new UnauthorizedException("Тhe user didn't vote !");
    }

    public Set<DisplayPostDto> getAllCommentPosts(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Set<DisplayPostDto> allCommentedPosts = new TreeSet<>((p1, p2) -> {
            if (p1.getId() == p2.getId()) return 0;
            return p2.getUploadDate().compareTo(p1.getUploadDate()) == 0 ? 1 : p2.getUploadDate().compareTo(p1.getUploadDate());
        });
        for (Comment c : user.getComments()) {
            allCommentedPosts.add(postServices.PostToDisplayPostDtoConversion(c.getPost()));
        }
        return allCommentedPosts;
    }

    public AllCommentsOnPostDto getAllCommentByPostId(long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Post not found !"));
        AllCommentsOnPostDto allComments = modelMapper.map(post, AllCommentsOnPostDto.class);
        allComments.setComments(allComments.getComments().stream().sorted((c1, c2) ->
                (int) (c2.getUpvotes() - c1.getUpvotes())).collect(Collectors.toList()));
        return allComments;
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

    public ResponseEntity<CommentResponseDto> removeComment(long commendId, HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        Optional<Comment> comment = commentRepository.findById(commendId);
        if (comment.isPresent()) {
            if (comment.get().getCommentOwner() != user && comment.get().getPost().getOwner() != user) {
                throw new UnauthorizedException("The user is not the owner of the comment!");
            }
            if (comment.get().getMediaUrl() != null) {
                File fileToDel = new File("media" + File.separator + "commentImages" + File.separator + comment.get().getMediaUrl());
                fileToDel.delete();
            }
            commentRepository.delete(comment.get());
            return ResponseEntity.ok(modelMapper.map(comment.get(), CommentResponseDto.class));
        }
        throw new NotFoundException("Comment not found !");
    }

    public ResponseEntity<UserWithCommentsDto> getAllCommentsUser(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        UserWithCommentsDto userWithCommentsDto = modelMapper.map(user, UserWithCommentsDto.class);
        userWithCommentsDto.setComments(userWithCommentsDto.getComments().stream()
                .sorted((c1, c2) -> (c2.getDateTime().compareTo(c1.getDateTime()))).collect(Collectors.toList()));
        return ResponseEntity.ok(userWithCommentsDto);
    }
}

