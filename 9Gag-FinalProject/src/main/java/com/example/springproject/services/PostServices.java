package com.example.springproject.services;

import com.example.springproject.controller.UserController;
import com.example.springproject.dto.categoryDtos.CategoryDto;
import com.example.springproject.dto.postDtos.DisplayPostDto;
import com.example.springproject.dto.postDtos.PostVoteResultsDto;
import com.example.springproject.dto.postDtos.PostWithoutOwnerDto;
import com.example.springproject.dto.userDtos.UserWithAllSavedPostDto;
import com.example.springproject.exceptions.BadRequestException;
import com.example.springproject.exceptions.NotFoundException;
import com.example.springproject.exceptions.UnauthorizedException;
import com.example.springproject.model.Category;
import com.example.springproject.model.Post;
import com.example.springproject.model.User;
import com.example.springproject.repositories.CategoryRepository;
import com.example.springproject.repositories.PostRepository;
import com.example.springproject.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.springproject.controller.PostController.POSTS_PER_PAGE;

@Service
public class PostServices {

    private static final String ONLY_WORDS_REGEX = "[^a-zA-Z]";
    private static final String URL_REGEX = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\\\+~#?&//=]{2,256}\\\\.[a-z]{2,6}\\\\b([-a-zA-Z0-9@:%._\\\\+~#?&//=]*)";
    private static final boolean PHOTO_AND_VIDEO = false;
    private static final int POST_MEDIA_MAX_SIZE = 1024 * 1024 * 100; //100 MB

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileServices fileServices;
    @Autowired
    private PostServices postServices;

    public DisplayPostDto createPost(String description, MultipartFile file, long userId, int categoryId) {
        Post p = postServices.validatePost(description, file, categoryId, userId);
        postRepository.save(p);
        DisplayPostDto pDto = postServices.PostToDisplayPostDtoConversion(p);
        return pDto;
    }

    public Post validatePost(String description, MultipartFile file, int categoryId, long userId) {

        String nameAndExt = postServices.saveMedia(file);
        if (description == null || description.isBlank() || description.length() <= 2) {
            throw new BadRequestException("post description is missing or is less than 3 symbols");
        }
        if (categoryId <= 0 || !categoryRepository.existsById((long) categoryId)) {
            throw new NotFoundException("category with id=" + categoryId + " doesn't exist");
        }
        if (userId <= 0 || !userRepository.existsById(userId)) {
            throw new NotFoundException("user with id=" + userId + " doesn't exist");
        }
        Post p = new Post();
        p.setDescription(description);
        p.setMediaUrl(nameAndExt);
        p.setCategory(categoryRepository.getById((long) categoryId));
        p.setOwner(userRepository.getById(userId));

        p.setDownvotes(0);
        p.setUpvotes(0);
        p.setUploadDate(LocalDateTime.now());
        return p;
    }

    public PostVoteResultsDto votePost(boolean isUpvote, long postId, long userId) {
        Post p = this.getPostById(postId);
        User u = userRepository.getById(userId);

        if (isUpvote) {
            if (u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
            } else if (u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            } else {
                p.getUpvoters().add(u);
                u.getUpvotedPosts().add(p);
                p.setUpvotes(p.getUpvotes() + 1);
            }
        } else {
            if (u.getDownvotedPosts().contains(p)) {//already downvoted
                u.getDownvotedPosts().remove(p);
                p.getDownvoters().remove(u);
                p.setDownvotes(p.getDownvotes() - 1);
            } else if (u.getUpvotedPosts().contains(p)) {//already upvoted
                u.getUpvotedPosts().remove(p);
                p.getUpvoters().remove(u);
                p.setUpvotes(p.getUpvotes() - 1);
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            } else {
                p.getDownvoters().add(u);
                u.getDownvotedPosts().add(p);
                p.setDownvotes(p.getDownvotes() + 1);
            }
        }
        postRepository.save(p);
        PostVoteResultsDto pDto = postServices.PostToVoteResultsPostsDtoConversion(p);
        return pDto;
    }

    public Post getPostById(long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("post with id=" + postId + " doesn't exist"));
    }

    public User savedPost(int postId, HttpServletRequest request) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getUserByRequest(request);
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

    public User unSavedPost(int postId, HttpServletRequest request) {
        Optional<Post> post = postRepository.findById((long) postId);
        User user = userRepository.getUserByRequest(request);
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

    public PostVoteResultsDto PostToVoteResultsPostsDtoConversion(Post p) {
        return modelMapper.map(p, PostVoteResultsDto.class);
    }

    public DisplayPostDto PostToDisplayPostDtoConversion(Post p) {
        DisplayPostDto pDto = modelMapper.map(p, DisplayPostDto.class);
        pDto.setUserId(p.getOwner().getId());
        pDto.setCategory(modelMapper.map(p.getCategory(), CategoryDto.class));
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
        /*
        serialize "search string" into keywords
        search in the descriptions of the posts for each word
        return posts sorted by most common keywords found first
        */

        ArrayList<String> words = this.extractWords(search); //length of the search
        ArrayList<String> descriptions = new ArrayList<>(postRepository.findAllPostDescriptions());
        ArrayList<Integer> postIds = new ArrayList<>(postRepository.findAllPostIds());

        for (String s : descriptions) { //test print
            System.out.println(s);
        }
        Map<Long, Integer> numberOfFoundWords = new HashMap<>();
        Map<Long, String> descsWithIds = new HashMap<>();

        for (int i = 0; i < postIds.size(); i++) { // 2 * number of posts
            numberOfFoundWords.put((long) postIds.get(i), 0);
            descsWithIds.put((long) postIds.get(i), descriptions.get(i).toLowerCase());
        }
        this.countFoundKeywords(words, numberOfFoundWords, descsWithIds);// number of descriptions * search * average length of description

        for (Map.Entry<Long, Integer> e : numberOfFoundWords.entrySet()) { //test print
            System.out.println(e.getKey() + " " + e.getValue());
        }
        System.out.println("@@@@@@@@@@@@"); //test print
        SortedSet<Map.Entry<Long, Integer>> sortedIds = this.sortIdsByFoundWords(numberOfFoundWords);
        List<DisplayPostDto> result = this.foundPostsFromSearch(sortedIds);
        return result;
    }

    private List<DisplayPostDto> foundPostsFromSearch(SortedSet<Map.Entry<Long, Integer>> sortedIds) {
        List<DisplayPostDto> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> e : sortedIds) {
            System.out.println(e.getKey() + " " + e.getValue()); //test print
            if (e.getValue() > 0) {
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
            if (s.length() > 0) {
                words.add(s.toLowerCase());
                System.out.println(s); // test print
            }
        }
        if (words.size() == 0) {
            throw new NotFoundException("No posts were found.");
        }
        return words;
    }

    private void countFoundKeywords(ArrayList<String> words, Map<Long, Integer> numberOfFoundWords, Map<Long, String> descWithId) {
        boolean anyKeywordsFound = false;
        for (String w : words) {

            for (Map.Entry<Long, String> e : descWithId.entrySet()) {
                int count = StringUtils.countMatches(e.getValue(), w);
                if(count > 0) {
                    numberOfFoundWords.put(e.getKey(), numberOfFoundWords.get(e.getKey()) + count);
                    anyKeywordsFound = true;
                }
            }
        }
        if (!anyKeywordsFound) {
            throw new NotFoundException("No posts were found.");
        }
    }

    @SneakyThrows
    public String saveMedia(MultipartFile file) {
        if (file.isEmpty()) {
            throw new UnauthorizedException("File is empty.");
        }
        if (file.getSize() > POST_MEDIA_MAX_SIZE) {
            throw new UnauthorizedException("File is too large. Limit is " + POST_MEDIA_MAX_SIZE / (1024*1024) + " mb");
        }
        fileServices.validateMediaType(file, PHOTO_AND_VIDEO);
        String name = String.valueOf(System.nanoTime());
        String ext = FilenameUtils.getExtension(file.getOriginalFilename());
        String nameAndExt = name + "." + ext;
        File destination = new File("media" + File.separator + "postMedia" + File.separator + nameAndExt);
        Files.copy(file.getInputStream(), Path.of(destination.toURI()));
        return nameAndExt;
    }

    public List<DisplayPostDto> allPostsByCategory(Category c, boolean isByUpvotes, int pageNumber) {
        List<DisplayPostDto> pDtos;
        List<Post> posts;
        if (isByUpvotes) {
            posts = postRepository.findAllByCategoryIdSortedByVotes(c.getId(), PageRequest.of(pageNumber, POSTS_PER_PAGE, Sort.by("votes").descending()));
            pDtos = postServices.PostToDisplayPostDtoConversionCollection(posts);
        } else {
            posts = postRepository.findAllByCategoryId(c.getId(), PageRequest.of(pageNumber, POSTS_PER_PAGE, Sort.by("upload_date").descending()));
            pDtos = postServices.PostToDisplayPostDtoConversionCollection(posts);
        }
        return pDtos;
    }

    public List<DisplayPostDto> getAllPosts(boolean isByUpvotes, int pageNumber) {
        List<Post> allPosts;
        if (isByUpvotes) {
            allPosts = postRepository.getAllOrderByUpvotes(PageRequest.of(pageNumber, POSTS_PER_PAGE));
        } else {
            allPosts = postRepository.getAllOrderByUploadDate(PageRequest.of(pageNumber, POSTS_PER_PAGE));
        }
        List<DisplayPostDto> pDtos = postServices.PostToDisplayPostDtoConversionCollection(allPosts);
        return pDtos;
    }

    public List<DisplayPostDto> getUpvotedPosts(long userId) {
        Set<Post> posts = userRepository.getById(userId).getUpvotedPosts();
        List<DisplayPostDto> pDtos = postServices.PostToDisplayPostDtoConversionCollection(postServices.sortPostsByDate(new ArrayList<>(posts)));
        return pDtos;
    }

    public void deletePost(HttpSession session, long id) {
        Post p = postServices.getPostById(id);
        if (p.getOwner().getId() == (Long) session.getAttribute(UserController.User_Id)) {//if current user is owner
            File fileToDel = new File("media" + File.separator + "postMedia" + File.separator + postRepository.getById(id).getMediaUrl());
            fileToDel.delete();
            postRepository.deleteById(id);
        } else {
            throw new UnauthorizedException("Only the owner of the post can delete it!");
        }
    }

    public UserWithAllSavedPostDto getAllSavedPosts(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        UserWithAllSavedPostDto userWithAllSavedPostDto = modelMapper.map(user, UserWithAllSavedPostDto.class);
        userWithAllSavedPostDto.setSavedPosts(userWithAllSavedPostDto.getSavedPosts()
                .stream().sorted((p1, p2) -> p2.getUploadDate().compareTo(p1.getUploadDate())).collect(Collectors.toList()));
        return userWithAllSavedPostDto;
    }

    public UserWithAllSavedPostDto getAllSavedPostsByVote(HttpServletRequest request) {
        User user = userRepository.getUserByRequest(request);
        UserWithAllSavedPostDto userWithAllSavedPostDto = modelMapper.map(user, UserWithAllSavedPostDto.class);
        userWithAllSavedPostDto.setSavedPosts(userWithAllSavedPostDto.getSavedPosts()
                .stream().sorted((p1, p2) -> p2.getUpvotes() - (p1.getUpvotes())).collect(Collectors.toList()));

        return userWithAllSavedPostDto;
    }

    public List<PostWithoutOwnerDto> getTrendingPosts(int pageNumber) {
        if(pageNumber < 0){
            throw new BadRequestException("Invalid page !");
        }
        List<Integer> posts = postRepository.trendingPosts(PageRequest.of(pageNumber,3));
        List<PostWithoutOwnerDto> postWithoutOwnerDtos = new ArrayList<>();
        for (Integer id:posts) {
            postWithoutOwnerDtos.add(modelMapper.map(postRepository.getById(id),PostWithoutOwnerDto.class));
        }
return postWithoutOwnerDtos;
    }
}
