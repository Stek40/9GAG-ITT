package com.example.springproject.services;

import com.example.springproject.dto.commentDtos.CommentWithMediaDto;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Comment;
import com.example.springproject.repositories.CommentRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

@Service
public class FileServices {

    @Autowired
    PostRepository postRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;


    public File getFileFromPost(long postId) {
        String fileName = postRepository.getMediaUrlOfPostWithId(postId);
        return new File("media" + File.separator + "postMedia" + File.separator + fileName);
    }

    @SneakyThrows
    public void validateMediaType(MultipartFile multipartFile, boolean onlyPhotoAllowed) {
        Tika tika = new Tika();
        String detectedType = tika.detect(multipartFile.getInputStream());
        System.out.println(detectedType); // print check
        if (onlyPhotoAllowed && !detectedType.contains("image")) {
            throw new UnauthorizedException("This media type is not allowed.");
        }
        if (!onlyPhotoAllowed && !detectedType.contains("video") && !detectedType.contains("image")) {
            throw new UnauthorizedException("This media type is not allowed.");
        }
    }

    public void downloadProfilePicture(HttpServletResponse response, HttpServletRequest request) {
        String filename = userRepository.getUserByRequest(request).getProfile_picture_url();
        File file = new File("media" + File.separator + "profilePictures" + File.separator + filename);
        try {
            Files.copy(file.toPath(), response.getOutputStream());
        } catch (IOException e) {
            throw new NotFoundException("Profile picture not found !");
        }
    }

    public ResponseEntity<CommentWithMediaDto> downloadCommentMedia(long cId, HttpServletResponse response) {
        Optional<Comment> comment = commentRepository.findById(cId);
        if (comment.isPresent()) {
            File file = new File("media" + File.separator + "commentImages" + File.separator + comment.get().getMediaUrl());
            try {
                Files.copy(file.toPath(), response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            CommentWithMediaDto commentWithMediaDto = new CommentWithMediaDto();
            commentWithMediaDto.setText(comment.get().getText());
            return ResponseEntity.ok(commentWithMediaDto);
        }
        throw new NotFoundException("Comment not found !");
    }
}

