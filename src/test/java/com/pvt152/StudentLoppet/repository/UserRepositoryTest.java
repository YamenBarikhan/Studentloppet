package com.pvt152.StudentLoppet.repository;

import com.pvt152.StudentLoppet.model.ProfilePicture;
import com.pvt152.StudentLoppet.model.User;
import com.pvt152.StudentLoppet.model.University;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY, connection = EmbeddedDatabaseConnection.H2)

public class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        @Test
        public void UserRepositoryTest_save_ReturnSavedUser() {

                User user = User.builder()
                                .email("test1@example.com")
                                .firstName("Leo")
                                .lastName("Smith")
                                .password("abc123")
                                .weight(75)
                                .score(10)
                                .build();

                User savedUser = userRepository.save(user);

                assertThat(savedUser).isNotNull();
                assertThat(savedUser.getEmail()).isEqualTo("test1@example.com");
                assertThat(savedUser.getFirstName()).isEqualTo("Leo");
                assertThat(savedUser.getLastName()).isEqualTo("Smith");
                assertThat(savedUser.getWeight()).isEqualTo(75);
                assertThat(savedUser.getPassword()).isEqualTo("abc123");

        }

        @Test
        public void UserRepositoryTest_GetAll_ReturnMoreThenOneUser() {

                User user = User.builder()
                                .email("test2@example.com")
                                .firstName("Leon")
                                .lastName("Hill")
                                .password("abc123")
                                .weight(75)
                                .score(10)
                                .build();

                User user2 = User.builder()
                                .email("test3@example.com")
                                .firstName("Malcolm ")
                                .lastName("Hunt")
                                .password("abc123")
                                .weight(75)
                                .score(10)
                                .build();

                userRepository.save(user);
                userRepository.save(user2);
                List<User> userList = (List<User>) userRepository.findAll();

                assertThat(userList).isNotNull();
                assertThat(userList.size()).isEqualTo(2);
        }

        @Test
        public void UserRepositoryTest_FindByID_ReturnUser() {

                User user = User.builder()
                                .email("test4@example.com")
                                .firstName("Roberto")
                                .lastName("Webb")
                                .password("abc123")
                                .weight(75)
                                .score(10)
                                .build();

                userRepository.save(user);
                User userID = userRepository.findById(user.getEmail()).get();

                assertThat(userID).isNotNull();

        }

        @Test
        public void UserRepositoryTest_findByUniversity_ReturnUserNotNull() {

                User user = User.builder()
                                .email("test5@example.com")
                                .score(100)
                                .firstName("Lee")
                                .lastName("Olson")
                                .password("abc123")
                                .weight(75)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();

                userRepository.save(user);
                List<User> usersByUniversity = userRepository.findByUniversity(University.UPPSALA_UNIVERSITET);

                assertThat(usersByUniversity).isNotEmpty();
                assertThat(usersByUniversity.get(0).getUniversity()).isEqualTo(University.UPPSALA_UNIVERSITET);
        }

        @Test
        public void UserRepositoryTest_findScoresByUser_ReturnsCorrectUserData() {

                ProfilePicture profilePicture1 = new ProfilePicture();
                profilePicture1.setFilename("pic1.jpg");
                profilePicture1.setMimeType("image/jpeg");
                ProfilePicture profilePicture2 = new ProfilePicture();
                profilePicture2.setFilename("pic2.jpg");
                profilePicture2.setMimeType("image/jpeg");

                User user1 = User.builder()
                                .email("test6@example.com")
                                .firstName("Tom")
                                .lastName("Hogan")
                                .password("abc123")
                                .weight(70)
                                .score(50)
                                .profilePicture(profilePicture1)
                                .build();
                User user2 = User.builder()
                                .email("test7@example.com")
                                .firstName("Anna")
                                .lastName("Bell")
                                .password("abc123")
                                .weight(65)
                                .score(75)
                                .profilePicture(profilePicture2)
                                .build();

                userRepository.save(user1);
                userRepository.save(user2);
                List<Object[]> result = userRepository.findScoresByUser();

                assertThat(result).isNotEmpty();
                assertThat(result.size()).isEqualTo(2);
                assertThat(result.get(0)[0]).isEqualTo("test7@example.com");
                assertThat(result.get(0)[1]).isEqualTo("Anna Bell");
                assertThat(result.get(0)[2]).isEqualTo(75);
                assertThat(result.get(1)[0]).isEqualTo("test6@example.com");
                assertThat(result.get(1)[1]).isEqualTo("Tom Hogan");
                assertThat(result.get(1)[2]).isEqualTo(50);
        }

        @Test
        public void UserRepositoryTest_findScoresByUniversity_ReturnsCorrectAggregatedScores() {

                User user1 = User.builder()
                                .email("test8@example.com")
                                .firstName("Cory")
                                .lastName("Shaw")
                                .password("abc123")
                                .weight(70)
                                .score(50)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user2 = User.builder()
                                .email("test9@example.com")
                                .firstName("Pete")
                                .lastName("Watkins")
                                .password("abc123")
                                .weight(65)
                                .score(75)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user3 = User.builder()
                                .email("test10@example.com")
                                .firstName("Rob")
                                .lastName("Lucci")
                                .password("abc123")
                                .weight(75)
                                .score(20)
                                .university(University.GÖTEBORGS_UNIVERSITET)
                                .build();

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                List<Object[]> results = userRepository.findScoresByUniversity();

                assertThat(results).isNotEmpty();
                assertThat(results.size()).isEqualTo(2);
                assertThat((University) results.get(0)[0]).isEqualTo(University.UPPSALA_UNIVERSITET);
                assertThat((Long) results.get(0)[1]).isEqualTo(125L);
                assertThat((University) results.get(1)[0]).isEqualTo(University.GÖTEBORGS_UNIVERSITET);
                assertThat((Long) results.get(1)[1]).isEqualTo(20L);
        }

        @Test
        public void UserRepositoryTest_countUsersByUniversity_ReturnsCorrectCounts() {

                User user1 = User.builder()
                                .email("user1@example.com")
                                .firstName("Alice")
                                .lastName("Johnson")
                                .password("abc123")
                                .weight(60)
                                .score(10)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user2 = User.builder()
                                .email("user2@example.com")
                                .firstName("Bob")
                                .lastName("Smith")
                                .password("abc123")
                                .weight(75)
                                .score(20)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user3 = User.builder()
                                .email("user3@example.com")
                                .firstName("Charlie")
                                .lastName("Brown")
                                .password("abc123")
                                .weight(70)
                                .score(30)
                                .university(University.GÖTEBORGS_UNIVERSITET)
                                .build();

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                List<Object[]> counts = userRepository.countUsersByUniversity();

                assertThat(counts).isNotEmpty();
                assertThat(counts.size()).isEqualTo(2);
                Object[] uppsalaResult = counts.stream()
                                .filter(result -> result[0].equals(University.UPPSALA_UNIVERSITET))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError("Uppsala Universitet not found"));
                Object[] goteborgResult = counts.stream()
                                .filter(result -> result[0].equals(University.GÖTEBORGS_UNIVERSITET))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError("Göteborgs Universitet not found"));
                assertThat(uppsalaResult[1]).isEqualTo(2L);
                assertThat(goteborgResult[1]).isEqualTo(1L);
        }

        @Test
        public void UserRepositoryTest_findScoresByUniversity_ReturnsCorrectScores() {
                User user1 = User.builder()
                                .email("user1@example.com")
                                .firstName("Alice")
                                .lastName("Johnson")
                                .password("abc123")
                                .weight(60)
                                .score(50)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user2 = User.builder()
                                .email("user2@example.com")
                                .firstName("Bob")
                                .lastName("Smith")
                                .password("abc123")
                                .weight(75)
                                .score(75)
                                .university(University.UPPSALA_UNIVERSITET)
                                .build();
                User user3 = User.builder()
                                .email("user3@example.com")
                                .firstName("Charlie")
                                .lastName("Brown")
                                .password("abc123")
                                .weight(70)
                                .score(20)
                                .university(University.GÖTEBORGS_UNIVERSITET)
                                .build();

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(user3);
                List<Object[]> results = userRepository.findScoresByUniversity();

                assertThat(results).isNotEmpty();
                assertThat(results.size()).isEqualTo(2);
                Object[] uppsalaResult = results.stream()
                                .filter(result -> result[0].equals(University.UPPSALA_UNIVERSITET))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError("Uppsala Universitet not found"));
                Object[] goteborgResult = results.stream()
                                .filter(result -> result[0].equals(University.GÖTEBORGS_UNIVERSITET))
                                .findFirst()
                                .orElseThrow(() -> new AssertionError("Göteborgs Universitet not found"));
                assertThat((Long) uppsalaResult[1]).isEqualTo(125L);
                assertThat((Long) goteborgResult[1]).isEqualTo(20L);
        }

}