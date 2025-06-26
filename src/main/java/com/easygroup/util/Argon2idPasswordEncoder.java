package com.easygroup.util;


import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Custom implementation of PasswordEncoder using Argon2id algorithm.
 * Argon2id is a memory-hard function that is more resistant to GPU attacks
 * and side-channel attacks than BCrypt.
 */
public class Argon2idPasswordEncoder implements PasswordEncoder {

    private static final int SALT_LENGTH = 16;
    private static final int HASH_LENGTH = 32;
    private static final int ITERATIONS = 3;
    private static final int MEMORY = 65536; // 64MB
    private static final int PARALLELISM = 4;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String encode(CharSequence rawPassword) {
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);

        byte[] hash = hash(rawPassword.toString().toCharArray(), salt);

        String encodedSalt = Base64.getEncoder().encodeToString(salt);
        String encodedHash = Base64.getEncoder().encodeToString(hash);

        return encodedSalt + "$" + encodedHash;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String[] parts = encodedPassword.split("\\$");
        if (parts.length != 2) {
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] expectedHash = Base64.getDecoder().decode(parts[1]);

        byte[] actualHash = hash(rawPassword.toString().toCharArray(), salt);

        return constantTimeEquals(expectedHash, actualHash);
    }

    private byte[] hash(char[] password, byte[] salt) {
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder(Argon2Parameters.ARGON2_id)
                .withSalt(salt)
                .withIterations(ITERATIONS)
                .withMemoryAsKB(MEMORY)
                .withParallelism(PARALLELISM);

        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        generator.init(builder.build());

        byte[] result = new byte[HASH_LENGTH];
        generator.generateBytes(password, result, 0, result.length);

        return result;
    }

    private boolean constantTimeEquals(byte[] expected, byte[] actual) {
        if (expected.length != actual.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < expected.length; i++) {
            result |= expected[i] ^ actual[i];
        }
        return result == 0;
    }
}
