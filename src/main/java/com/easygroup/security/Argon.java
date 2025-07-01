package com.easygroup.security;

import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class Argon implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(1)
                .withMemoryAsKB(65536)
                .withIterations(3);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] hash = new byte[32];
        generator.generateBytes(rawPassword.toString().getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return Base64.getEncoder().encodeToString(salt) + "$" + Base64.getEncoder().encodeToString(hash);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 2) return false;

        byte[] salt = Base64.getDecoder().decode(parts[0]);

        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withParallelism(1)
                .withMemoryAsKB(65536)
                .withIterations(3);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] hash = new byte[32];
        generator.generateBytes(rawPassword.toString().getBytes(StandardCharsets.UTF_8), hash, 0, hash.length);

        return encodedPassword.equals(parts[0] + "$" + Base64.getEncoder().encodeToString(hash));
    }
}
