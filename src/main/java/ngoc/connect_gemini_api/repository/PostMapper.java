package ngoc.connect_gemini_api.repository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import ngoc.connect_gemini_api.model.Post;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {
    void insert(Post post);
    Optional<Post> findById(int id);
    List<Post> findAll();
    List<Post> findByUserId(int userId);
    List<Post> findPostsForUser(@Param("userId") Integer userId);
    void update(Post post);
    void deleteById(int id);
    List<Post> findPostsWithPagination(@Param("userId") Integer userId, @Param("pageable")Pageable pageable);
    long countPosts(@Param("userId") Integer userId);
}
