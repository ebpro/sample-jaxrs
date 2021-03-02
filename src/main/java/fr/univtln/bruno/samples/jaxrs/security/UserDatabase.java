package fr.univtln.bruno.samples.jaxrs.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.ToString;
import lombok.extern.java.Log;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

@Log
@ToString
public class UserDatabase {
    public static final UserDatabase USER_DATABASE = new UserDatabase();

    // We need a signing key for the id token, so we'll create one just for this example. Usually
    // the key would be read from your application configuration instead.
    public static final Key KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    static {
        try {
            USER_DATABASE.addUser("John", "Doe", "john.doe@nowhere.com", "admin", EnumSet.of(Role.ADMIN));
            USER_DATABASE.addUser("William", "Smith", "william.smith@here.net", "user", EnumSet.of(Role.USER));
            USER_DATABASE.addUser("Mary", "Robert", "mary.roberts@here.net", "user", EnumSet.of(Role.USER));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private final Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        USER_DATABASE.users.values().forEach(u->log.info(u.toString()));
    }

    public void addUser(String firstname, String lastname, String email, String password, EnumSet<Role> roles)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        users.put(email, User.builder().firstName(firstname).lastName(lastname).email(email).password(password).roles(roles).build());
    }

    public Map<String, User> getUsers() {
        return Collections.unmodifiableMap(users);
    }

    public void removeUser(String email) {
        users.remove(email);
    }

    public boolean checkPassword(String email, String password) throws InvalidKeySpecException, NoSuchAlgorithmException {
        return users.get(email).checkPassword(password);
    }

    public User getUser(String email) {
        return users.get(email);
    }

    public Set<Role> getUserRoles(String email) {
        return users.get(email).getRoles();
    }

    public enum Role {ADMIN, USER, GUEST}
}
