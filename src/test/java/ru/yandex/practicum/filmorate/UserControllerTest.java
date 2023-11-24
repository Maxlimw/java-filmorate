package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {

    private final UserService userService;
    private static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void shouldCreateUser() {
        User user = User.builder()
                .login("NewUser1")
                .name("John")
                .email("john@example.com")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        userService.create(user);

        assertEquals("john@example.com", userService.find(user.getId()).getEmail());
    }

    @Test
    public void shouldUpdateUser() {
        User user = User.builder()
                .login("NewUser2")
                .name("Jane")
                .email("jane@example.com")
                .birthday(LocalDate.of(1995, 5, 5))
                .build();
        userService.create(user);

        User userUpdate = User.builder()
                .id(user.getId())
                .login("UpdatedUser3")
                .name("Anna")
                .email("anna@example.com")
                .birthday(LocalDate.of(1988, 8, 8))
                .build();
        userService.update(userUpdate);

        assertEquals(userUpdate, userService.find(user.getId()));
    }

    @Test
    public void shouldCreateUserWithEmptyName() {
        User user = User.builder()
                .login("NewUser4")
                .email("user4@example.com")
                .birthday(LocalDate.of(1992, 2, 2))
                .build();

        userService.create(user);

        assertEquals("NewUser4", user.getName());
    }

    @Test
    void shouldNotPassEmailValidation() {
        User user = User.builder()
                .login("NewUser5")
                .name("Melissa")
                .email("invalidemail")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithEmptyLogin() {
        User user = User.builder()
                .login("")
                .name("Melissa")
                .email("user6@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldNotPassLoginValidationWithBlanksInLogin() {
        User user = User.builder()
                .login("  InvalidLogin ")
                .name("Melissa")
                .email("user7@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();

        assertThrows(ValidationException.class, () -> userService.create(user));
    }

    @Test
    public void shouldNotPassBirthdayValidation() {
        User user = User.builder()
                .login("NewUser6")
                .name("Melissa")
                .email("user8@example.com")
                .birthday(LocalDate.of(3000, 8, 15))
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void shouldAddFriend() {
        User user1 = User.builder()
                .login("FriendUser1")
                .name("Melissa")
                .email("friend1@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user1);

        User user2 = User.builder()
                .login("FriendUser2")
                .name("Melissa")
                .email("friend2@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user2);

        userService.addFriend(user1.getId(), user2.getId());
    }

    @Test
    public void shouldDeleteFriend() {
        User user = User.builder()
                .login("FriendUser3")
                .name("Melissa")
                .email("friend3@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);

        User friend = User.builder()
                .login("FriendUser4")
                .name("Melissa")
                .email("friend4@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);

        userService.addFriend(user.getId(), friend.getId());
        userService.removeFriend(user.getId(), friend.getId());

        assertNull(user.getFriends());
        assertNull(friend.getFriends());
    }

    @Test
    public void shouldFindMutualFriend() {
        User user = User.builder()
                .login("MutualUser1")
                .name("Melissa")
                .email("mutual1@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("MutualUser2")
                .name("Melissa")
                .email("mutual2@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(friend);
        User mutualFriend = User.builder()
                .login("MutualUser3")
                .name("Melissa")
                .email("mutual3@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(mutualFriend);

        userService.addFriend(user.getId(), mutualFriend.getId());
        userService.addFriend(friend.getId(), mutualFriend.getId());

        List<User> mutual = userService.findCommonFriends(user.getId(), friend.getId());

        assertEquals(List.of(mutualFriend), mutual);
    }

    @Test
    public void shouldReturnAllFriends() {
        User user = User.builder()
                .login("AllFriendsUser1")
                .name("Melissa")
                .email("allfriends1@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
        userService.create(user);
        User friend = User.builder()
                .login("AllFriendsUser2")
                .name("Melissa")
                .email("allfriends2@example.com")
                .birthday(LocalDate.of(2000, 8, 15))
                .build();
    }
}

