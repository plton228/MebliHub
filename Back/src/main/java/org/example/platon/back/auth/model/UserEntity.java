package org.example.platon.back.auth.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "users")
public class UserEntity {
    @Id
    private String id;
    private String name;
    private String lastName;
    @Indexed(unique = true)
    private String email;
    private String password;
    private Set<Role> role;

    public UserEntity() {}

    public UserEntity(String id, String name, String lastName, String email, String password, Set<Role> role) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Role> getRole() { return role; }
    public void setRole(Set<Role> role) { this.role = role; }
}
