package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.dto.CategoryWithoutPostsDto;
//import com.example.springproject.dto.PostWithCategoryDto;
//import com.example.springproject.dto.PostWithoutOwnerDto;
import com.example.springproject.dto.UserWithoutPostsDto;
import com.example.springproject.dto.newDtos.postDtos.DisplayPostDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostServices {

    private static final String ONLY_WORDS_REGEX = "[^a-zA-Z]";
    private static final String URL_REGEX = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Post create(String description, String mediaUrl, int categoryId, long userId) {

        if(description == null || description.isBlank() || description.length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if(mediaUrl == null || mediaUrl.matches(URL_REGEX)){
            throw new BadRequestException("post media url is missing or is not correct");
        }
        if(categoryId <= 0 || !categoryRepository.existsById((long)categoryId)) {
            throw new NotFoundException("category with id=" + categoryId + " doesn't exist");
        }
        if(userId <= 0 || !userRepository.existsById(userId)) {
            throw new NotFoundException("user with id=" + userId + " doesn't exist");
        }
        Post p = new Post();
        p.setDescription(description);
        p.setMediaUrl(mediaUrl);
        p.setCategory(categoryRepository.getById((long) categoryId));
        p.setOwner(userRepository.getById(userId));

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());
        return p;
    }
    public Post votePost(boolean isUpvote, long postId, long userId) {
        Post p = this.getPostById(postId);
        User u = userRepository.getById(userId);

        if(isUpvote) {
            if(u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
            }
            else if(u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            }
            else {
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            }
        }
        else {
            if(u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
            }
            else if(u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            }
            else {
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            }
        }
        return p;
    }
    public Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("post with id=" + postId + "doesn't exist"));
    }

    public User savedPost(int postId, long userId) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getById(userId);
        if (post.isPresent()) {
            if (user.getSavedPosts().contains(post.get())) {
                throw new BadRequestException("User already saved this post !");
            }

            post.get().getSavedUser().add(user);
            postRepository.save(post.get());
            return user;
        }
        throw new NotFoundException("Post not found !");
    }

    public User unSavedPost(int postId, Long userId) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getById(userId);
        if (post.isPresent()) {
            if (user.getSavedPosts().contains(post.get())) {
                user.getSavedPosts().remove(post.get());
                post.get().getSavedUser().remove(user);
                postRepository.save(post.get());
                return user;
            }
        }
        throw new NotFoundException("Post not found !");
    }

    public List<Post> sortPostsByDate(List<Post> allPosts) {
        allPosts.sort((p1, p2) -> {
            return p2.getUploadDate().compareTo(p1.getUploadDate());
        });
        return allPosts;
    }

    public DisplayPostDto PostToDisplayPostDtoConversion(Post p) {
        DisplayPostDto pDto = modelMapper.map(p, DisplayPostDto.class);
        pDto.setUserId(p.getOwner().getId());
        pDto.setCategory(p.getCategory().getName());
        return pDto;
    }
    public List<DisplayPostDto> PostToDisplayPostDtoConversionCollection(List<Post> posts) {
        List<DisplayPostDto> pDtos = new ArrayList<>();
        for (Post p : posts) {
            pDtos.add(PostToDisplayPostDtoConversion(p));
        }
        return pDtos;
    }
    public List<DisplayPostDto> searchPostGenerator(String search) {
        ArrayList<String> words = this.extractWords(search);

        ArrayList<String> descriptions = new ArrayList<>(postRepository.findAllPostDescriptions());
        ArrayList<Integer> ids = new ArrayList<>(postRepository.findAllPostIds());

        for (String s : descriptions) { //test print
            System.out.println(s);
        }
        Map<Long, Integer> numberOfFoundWords = new HashMap<>();
        Map<Long, String> descWithId = new HashMap<>();

        for (int i = 0; i < ids.size(); i++) {
            numberOfFoundWords.put((long)ids.get(i), 0);
            descWithId.put((long)ids.get(i), descriptions.get(i).toLowerCase());
        }
        this.countFoundKeywords(words, numberOfFoundWords, descWithId);

        for (Map.Entry<Long, Integer> e : numberOfFoundWords.entrySet()) { //test print
            System.out.println(e.getKey() + " " + e.getValue());
        }
        System.out.println("@@@@@@@@@@@@");
        SortedSet<Map.Entry<Long, Integer>> sortedIds = this.sortIdsByFoundWords(numberOfFoundWords);
        List<DisplayPostDto> result = this.foundPostsFromSearch(sortedIds);
       return result;
    }

    private List<DisplayPostDto> foundPostsFromSearch(SortedSet<Map.Entry<Long, Integer>> sortedIds) {
        List<DisplayPostDto> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : sortedIds) {
            System.out.println(e.getKey() + " " + e.getValue()); //test print
            if(e.getValue() > 0) {
                //result.add(this.PostToDtoConversion2(postRepository.getById(e.getKey())));
                result.add(this.PostToDisplayPostDtoConversion(postRepository.getById(e.getKey())));
            }
        }
        return result;
    }

    private SortedSet<Map.Entry<Long, Integer>> sortIdsByFoundWords(Map<Long, Integer> numberOfFoundWords) {
        SortedSet<Map.Entry<Long, Integer>> sortedIds = new TreeSet<>((n1, n2) -> {
            return (n2.getValue().equals(n1.getValue())) ? 1 : n2.getValue().compareTo(n1.getValue());
        });
        sortedIds.addAll(numberOfFoundWords.entrySet());
        return sortedIds;
    }

    private ArrayList<String> extractWords(String search) {
        String[] wrds = search.split(ONLY_WORDS_REGEX);
        ArrayList<String> words = new ArrayList<>();
        for (String s : wrds) {
            if(s.length() > 0 ) {
                words.add(s.toLowerCase());
                System.out.println(s);
            }
        }
        if(words.size() == 0) {
            throw new NotFoundException("No posts were found.");
        }
        return words;
    }

    private void countFoundKeywords(ArrayList<String> words, Map<Long, Integer> numberOfFoundWords, Map<Long, String> descWithId) {
        boolean anyKeywordsFound = false;
        for (String w : words) {

            for (Map.Entry<Long, String> e : descWithId.entrySet()) {
                if(e.getValue().contains(w)) {
                    numberOfFoundWords.put(e.getKey(), numberOfFoundWords.get(e.getKey()) + 1);
                    anyKeywordsFound = true;
                }
            }
        }
        if(!anyKeywordsFound) {
            throw new NotFoundException("No posts were found.");
        }
    }

    public String saveMedia(MultipartFile file) throws IOException {
        if(file.isEmpty()) {
            throw new NotFoundException("Media is missing.");
        }
        String name = String.valueOf(System.nanoTime());
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String nameAndExt = name + "." + ext;
        File destination = new File("media" + File.separator + "postMedia" + File.separator + nameAndExt);
        Files.copy(file.getInputStream(), Path.of(destination.toURI()));
        return nameAndExt;
    }

    public ArrayList<Post> postsSetToList(Set<Post> set) {
        return new ArrayList<>(set);
    }
}
