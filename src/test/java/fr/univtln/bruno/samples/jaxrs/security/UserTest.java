
package fr.univtln.bruno.samples.jaxrs.security;

import org.junit.Test;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.EnumSet;

import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void testBuilder() throws InvalidKeySpecException, NoSuchAlgorithmException {
        String lastname="Doe", firstname="John", email="j.d@here.com", password="mypass";
            User user  = User.builder()
                    .lastName(lastname)
                    .firstName(firstname)
                    .email(email)
                    .password(password)
                    .roles(EnumSet.of(InMemoryLoginModule.Role.ADMIN))
                    .build();
            assertTrue(user.checkPassword(password));
            assertTrue(user.contains(InMemoryLoginModule.Role.valueOf("ADMIN")));
    }
}