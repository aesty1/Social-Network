package ru.denis.social_network.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class MyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(nullable = false, length = 128)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyPost> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyFriend> friends;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "bio")
    private String bio;

    @Column(name = "nickname")
    private String nickname;

    public MyUser(String email, String name, String password, String avatar, String bio, String nickname) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.avatar = avatar;
        this.bio = bio;
        this.nickname = nickname;
    }

    public MyUser() {
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public List<MyPost> getPosts() {
        return posts;
    }

    public List<MyFriend> getFriends() {
        return friends;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setPosts(List<MyPost> posts) {
        this.posts = posts;
    }

    public void setFriends(List<MyFriend> friends) {
        this.friends = friends;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getBio() {
        return bio;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}