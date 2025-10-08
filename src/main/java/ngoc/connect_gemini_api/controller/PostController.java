package ngoc.connect_gemini_api.controller;

import jakarta.servlet.http.HttpSession;
import ngoc.connect_gemini_api.dto.request.post.SavePostRequest;
import ngoc.connect_gemini_api.dto.request.post.UpdatePostRequest;
import ngoc.connect_gemini_api.dto.response.ApiResponse;
import ngoc.connect_gemini_api.model.Post;
import ngoc.connect_gemini_api.model.User;
import ngoc.connect_gemini_api.service.PostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    /*
     * Sử dụng ép kiểu trong java: Cách 1 đặt class Object cha chung của tất cả các class.
     * Kiểm tra null và ép kiểu User cho biến currentUser hoặc nhận giá trị null
     */
    @PostMapping("/api/save-post")
    public ResponseEntity<ApiResponse<Object>> savePost(SavePostRequest request, HttpSession session) {
        Object loggedInUser = session.getAttribute("loggedInUser");
        try {
            postService.savePostContent(
                    request,
                    (loggedInUser instanceof User currentUser) ? currentUser : null
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Post saved successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/api/update-post")
    public ResponseEntity<ApiResponse<Object>> updatePost(UpdatePostRequest request){
        try {
            postService.updatePostContent(request);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Post updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/api/delete-post/{id}")
    public ResponseEntity<ApiResponse<Object>> deletePost(@PathVariable int id) {
        try {
            postService.deletePost(id);
            return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success("Post deleted successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    /*
    * Sử dụng ép kiểu trong java: Cách 2 sử dụng annotation @SessionAttribute("tên key",required = false -> nhận giá trị null nếu không tồn tại)
    * lúc này biến loggedInUser là null hoặc là đối tượng User có giá trị
    */
    @GetMapping("/api/posts")
    public ResponseEntity<Page<Post>> getPosts(Model model, HttpSession session,
                                               @SessionAttribute(name="loggedInUser",required = false)User loggedInUser,
                                               Pageable pageable) {
        Page<Post> posts = postService.getPosts(loggedInUser, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/api/post/{id}")
    public ResponseEntity<Post> getPost(Model model, @SessionAttribute(name="loggedInUser", required = false)User loggedInUser, @PathVariable int id) {
        Post post = null;
        try {
            post = postService.getPost(id);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
