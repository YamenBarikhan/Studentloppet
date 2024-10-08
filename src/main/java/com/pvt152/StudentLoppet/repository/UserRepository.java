package com.pvt152.StudentLoppet.repository;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import jakarta.transaction.Transactional;

import com.pvt152.StudentLoppet.model.University;
import com.pvt152.StudentLoppet.model.User;

public interface UserRepository extends CrudRepository<User, String> {

    @Query("SELECT u.university as university, SUM(u.score) as totalScore FROM User u WHERE u.university IS NOT NULL GROUP BY u.university ORDER BY SUM(u.score) DESC")
    List<Object[]> findScoresByUniversity();

    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.password = ?2 where u.email = ?1")
    void updatePassword(String email, String password);

    @Query("SELECT u.score FROM User u WHERE u.email = :email")
    Integer findScoreByEmail(@Param("email") String email);

    @Query("SELECT u.university as university, COUNT(u) as userCount FROM User u WHERE u.university IS NOT NULL GROUP BY u.university ORDER BY COUNT(u) DESC")
    List<Object[]> countUsersByUniversity();

    @Query("SELECT u FROM User u WHERE u.university = :university")
    List<User> findByUniversity(@Param("university") University university);

    @Query("SELECT u.email, CONCAT(u.firstName, ' ', u.lastName), u.score, u.profilePicture FROM User u ORDER BY u.score DESC")
    List<Object[]> findScoresByUser();

    @Query("SELECT u.email, u.score FROM User u WHERE u.university = :university ORDER BY u.score DESC")
    List<Object[]> findScoresByUniversity(@Param("university") University university);

    User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :fullName AND u.yearOfBirth = :yearOfBirth")
    User findByFullNameAndYearOfBirth(@Param("fullName") String fullName, @Param("yearOfBirth") int yearOfBirth);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) = :fullName AND u.yearOfBirth = :yearOfBirth")
    boolean existsByFullNameAndYearOfBirth(@Param("fullName") String fullName, @Param("yearOfBirth") int yearOfBirth);
}