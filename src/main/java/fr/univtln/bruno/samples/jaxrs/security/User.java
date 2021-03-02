package fr.univtln.bruno.samples.jaxrs.security;

import lombok.*;
import lombok.experimental.Delegate;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

@Log
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "email")
public class User implements Principal {
    UUID uuid = UUID.randomUUID();
    String firstName, lastName, email;
    byte[] passwordHash;
    byte[] salt = new byte[16];

    @Delegate
    EnumSet<UserDatabase.Role> roles;

    SecureRandom random = new SecureRandom();

    @Builder
    public User(String firstName, String lastName, String email, String password, EnumSet<UserDatabase.Role> roles)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = roles;

        random.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        passwordHash = factory.generateSecret(spec).getEncoded();
    }

    @Override
    public String getName() {
        return lastName + ", " + firstName+" <"+email+">";
    }

    public String toString() {
        return email + "" + Base64.getEncoder().encodeToString(passwordHash);
    }

    public boolean checkPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] submittedPasswordHash = factory.generateSecret(spec).getEncoded();
        return Arrays.equals(passwordHash, submittedPasswordHash);
    }
}