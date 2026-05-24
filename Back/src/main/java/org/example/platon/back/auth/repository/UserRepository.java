package org.example.platon.back.auth.repository;

import org.example.platon.back.auth.model.Role;
import org.example.platon.back.auth.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

    // ── Derived query methods ───────────────────────────────────────────────
    // { "email": email }
    Optional<UserEntity> findByEmail(String email);

    // { "email": email } — exists check without loading the document
    boolean existsByEmail(String email);

    // { "role": { $in: [role] } } — Set<Role> contains the given role
    List<UserEntity> findByRoleContaining(Role role);

    // COUNT WHERE role contains role
    long countByRoleContaining(Role role);

    // ── @Query — явний MongoDB JSON-запит ────────────────────────────────────

    // Знаходить всіх адміністраторів, повертає тільки email та ім'я
    @Query(
        value  = "{ 'role': 'ADMIN' }",
        fields = "{ 'email': 1, 'name': 1, 'lastName': 1 }"
    )
    List<UserEntity> findAllAdmins();

    // Знаходить юзерів, email яких містить заданий фрагмент
    @Query("{ 'email': { $regex: ?0, $options: 'i' } }")
    List<UserEntity> findByEmailContaining(String emailFragment);

    // Юзери, які мають лише роль USER (не адміни)
    @Query("{ 'role': { $size: 1, $all: ['USER'] } }")
    List<UserEntity> findPureUsers();
}
