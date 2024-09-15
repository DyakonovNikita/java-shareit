package ru.practicum.shareit.request.model;

import jakarta.persistence.*;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
@Entity
@Table(name = "requests")
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;

    @Column(nullable = false, length = 1024)
    private String description;

    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "user_id", nullable = false)
    private User requester;
}
