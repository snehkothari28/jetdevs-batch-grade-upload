package com.jetdevs.batchgradeupload.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
/**
 * Custom password encoder implementing HMAC-SHA-512 algorithm with a provided salt.
 * Implements Spring Security's PasswordEncoder interface.
 */
public class Hmac512PasswordEncoder implements PasswordEncoder {

    // Prefix for the encoded password to indicate HMAC-SHA-512 encoding
    private static final String SSHA512_PREFIX = "{SSHA-512}";
    // HMAC algorithm used for password encoding
    private static final String HMAC_SHA512 = "HmacSHA512";
    // Salt used for password hashing
    private final String salt;
    // Logger for logging messages and errors
    Logger logger = LoggerFactory.getLogger(Hmac512PasswordEncoder.class);

    /**
     * Constructor for Hmac512PasswordEncoder.
     *
     * @param salt Salt value used for password hashing.
     */
    public Hmac512PasswordEncoder(String salt) {
        if (salt == null) {
            throw new IllegalArgumentException("salt cannot be null");
        }
        this.salt = salt;
    }

    /**
     * Encodes the raw password using HMAC-SHA-512 algorithm with the provided salt.
     *
     * @param rawPassword Raw password to be encoded.
     * @return Encoded password with HMAC-SHA-512 algorithm and salt.
     */
    public String encode(CharSequence rawPassword) {
        String result = null;

        try {
            // Initialize HMAC-SHA-512 algorithm with the provided salt
            Mac sha512Hmac = Mac.getInstance(HMAC_SHA512);
            final byte[] byteKey = Utf8.encode(salt);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            // Generate MAC (Message Authentication Code) data for the raw password
            byte[] macData = sha512Hmac.doFinal(Utf8.encode(rawPassword.toString()));

            // Add prefix and encode the MAC data using Base64
            result = SSHA512_PREFIX + Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            // Handle errors during password encoding
            logger.error("Error encoding password", e);
        }

        return result;
    }

    /**
     * Matches the raw password with the encoded password.
     *
     * @param rawPassword     Raw password entered by the user.
     * @param encodedPassword Encoded password stored in the database.
     * @return True if the raw password matches the encoded password, false otherwise.
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        // Extract the encoded password without the prefix
        String encodedRawPass = extractEncodedPassword(encode(rawPassword));
        // Compare the raw password's encoded form with the stored encoded password
        return MessageDigest.isEqual(Utf8.encode(encodedRawPass), Utf8.encode(encodedPassword));
    }

    /**
     * Extracts the encoded password by removing the prefix.
     *
     * @param prefixEncodedPassword Encoded password with the prefix.
     * @return Encoded password without the prefix.
     */
    private String extractEncodedPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(SSHA512_PREFIX);
        return prefixEncodedPassword.substring(start + SSHA512_PREFIX.length());
    }
}
