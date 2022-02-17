package net.pauljackals.springblog.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "first name must not be empty")
    @Size(max = 32, message = "first name must not be longer than 32 characters")
    private String firstName;

    @NotBlank(message = "last name must not be empty")
    @Size(max = 32, message = "last name must not be longer than 32 characters")
    private String lastName;

    @NotNull(message = "username must not be empty")
    @Pattern(regexp = "^\\w{1,32}$", message = "username must not be empty nor longer than 32 letters/numbers")
    @Column(unique = true)
    private String username;
    
    @NotBlank(message = "email must not be empty")
    @Email(message = "email must be valid")
    @Column(unique = true)
    private String email;

    @NotNull(message = "password must not be empty")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*\\p{javaLowerCase})(?=.*\\p{javaUpperCase})(?=.*[!@#&()-[{}]:;',?/*~$^+=<>]).{8,128}$", message = "password must include an uppercase letter, a lowercase letter, a digit and a special character, must not be shorter than 8 nor longer than 128 characters")
    private String password;

    public Author(String firstName, String lastName, String username, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
