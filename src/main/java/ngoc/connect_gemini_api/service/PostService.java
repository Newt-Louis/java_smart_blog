package ngoc.connect_gemini_api.service;

import ngoc.connect_gemini_api.dto.request.post.SavePostRequest;
import ngoc.connect_gemini_api.dto.request.post.UpdatePostRequest;
import ngoc.connect_gemini_api.model.Post;
import ngoc.connect_gemini_api.model.User;
import ngoc.connect_gemini_api.repository.PostMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {
    private final PostMapper postMapper;
    private final GcsStorageService storageService;

    public PostService(PostMapper postMapper, GcsStorageService storageService) {
        this.postMapper = postMapper;
        this.storageService = storageService;
    }

    public void savePostContent(SavePostRequest request, User currentUser) {
        int userId = currentUser != null ? currentUser.getId() : 0;
        String processedHTML = storageService.processAndCommitImages(request.getContent(),userId);
        Post newPost = new Post();
        newPost.setTitle(request.getTitle() != null ? request.getTitle() : null);
        newPost.setContent(processedHTML);
        newPost.setUserId(userId);
        postMapper.insert(newPost);
    }

    public void updatePostContent(UpdatePostRequest request) {
        Post oldPost = getPost(request.getId());
        oldPost.setContent(request.getContent());
        oldPost.setTitle(request.getTitle() != null ? request.getTitle() : null);
        postMapper.update(oldPost);
    }

    public void deletePost(int id) {
        getPost(id);
        postMapper.deleteById(id);
    }

    public List<Post> getPosts(User currentUser) {
        Integer userId = (currentUser != null) ? currentUser.getId() : null;
        return postMapper.findPostsForUser(userId);
    }

    public Post getPost(int id) {
        return postMapper.findById(id).orElseThrow(()->new RuntimeException("Post not found!"));
    }

    public Page<Post> getPosts(User currentUser, Pageable pageable) {
        Integer userId = (currentUser != null) ? currentUser.getId() : null;
        List<Post> posts = postMapper.findPostsWithPagination(userId, pageable);
        long totalPosts = postMapper.countPosts(userId);
        return new PageImpl<>(posts, pageable, totalPosts);
    }
}
