package ru.denis.social_network.models.requests;

import jakarta.persistence.*;
import ru.denis.social_network.models.MyUser;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "friend_requests")
public class MyFriendRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private MyUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private MyUser receiver;

    @Column()
    private String status;

    @Column(name = "created_at")
    private LocalDateTime created_at = LocalDateTime.now();

    public MyFriendRequest() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MyUser getSender() {
        return sender;
    }

    public void setSender(MyUser sender) {
        this.sender = sender;
    }

    public MyUser getReceiver() {
        return receiver;
    }

    public void setReceiver(MyUser receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
