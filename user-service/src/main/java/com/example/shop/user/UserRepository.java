package com.example.shop.user;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class UserRepository {

    private final ConcurrentMap<String, User> store = new ConcurrentHashMap<>();

    public UserRepository() {
        save(new User("u-1", "Jeff Bezos", "jeff@example.com", "410 Terry Ave N, Seattle, WA"));
        save(new User("u-2", "Ada Lovelace", "ada@example.com", "12 Computing St, London"));
        save(new User("u-3", "Alan Turing", "alan@example.com", "78 Enigma Rd, Manchester"));
    }

    public Collection<User> findAll() {
        return store.values();
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public User save(User user) {
        store.put(user.getId(), user);
        return user;
    }
}
