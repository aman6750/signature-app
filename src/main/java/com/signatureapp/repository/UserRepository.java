package com.signatureapp.repository;

import com.signatureapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);  //We'll use this to check "is this email already registered?" during signup.
}

/*

Explanation — this is genuinely amazing
interface UserRepository — Notice it's an interface, not a class. You'll never implement these methods. Spring auto-generates the code for you at runtime. 🤯
extends JpaRepository<User, Long> — By extending JpaRepository, you instantly get these methods for free (without writing them):

save(user) — insert or update
findById(id) — get user by ID
findAll() — get all users
deleteById(id) — delete user
count() — count users
...and ~20 more

The two generic types mean: "This repository manages User entities, whose ID type is Long."
@Repository — Marks this as a Spring component. Spring will:

Detect this interface at startup
Auto-generate the implementation code
Make it available to inject into other classes (Services)

Optional<User> findByEmail(String email) — Spring Data JPA reads this method name and automatically generates the SQL query:
sqlSELECT * FROM users WHERE email = ?
You don't write the SQL. The method name IS the query. This pattern is called "derived query methods".
Optional<User> — Means the result might be empty (user not found). Java's way of saying "this could be null" safely, forcing you to handle the empty case. No more NullPointerException.
boolean existsByEmail(String email) — Returns true/false. Auto-generates:
sqlSELECT COUNT(*) > 0 FROM users WHERE email = ?
We'll use this to check "is this email already registered?" during signup.
 */