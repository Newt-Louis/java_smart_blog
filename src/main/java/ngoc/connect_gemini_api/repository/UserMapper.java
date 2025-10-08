package ngoc.connect_gemini_api.repository;

import org.apache.ibatis.annotations.Mapper;
import ngoc.connect_gemini_api.model.User;
import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByPassword(String password);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    User insert(User user);
    void update(User user);
    void deleteById(Long id);
}
