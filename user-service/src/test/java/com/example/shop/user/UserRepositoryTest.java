package com.example.shop.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest {

    private final UserRepository repo = new UserRepository();

    @Test
    void seedsThreeUsers() {
        assertThat(repo.findAll()).hasSize(3);
        assertThat(repo.findById("u-1")).isPresent();
    }

    @Test
    void findByIdReturnsEmptyForUnknownUser() {
        assertThat(repo.findById("u-does-not-exist")).isEmpty();
    }

    @Test
    void saveAddsNewUser() {
        repo.save(new User("u-9", "New Person", "new@example.com", "1 Test St"));

        assertThat(repo.findAll()).hasSize(4);
        assertThat(repo.findById("u-9")).get()
                .extracting(User::getEmail).isEqualTo("new@example.com");
    }
}
