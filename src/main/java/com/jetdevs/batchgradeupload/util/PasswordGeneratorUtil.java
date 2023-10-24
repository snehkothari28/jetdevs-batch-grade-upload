package com.jetdevs.batchgradeupload.util;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * Utility class for generating secure random passwords.
 */
public class PasswordGeneratorUtil {
    // Secure random number generator
    private static final Random random = new SecureRandom();

    /**
     * Generates a secure random password consisting of alphabets (upper and lower case),
     * numbers, and special characters.
     *
     * @return Secure random password.
     */
    public static String generateSecureRandomPassword() {
        // Combine random numbers, special characters, and alphabets to create a secure password
        Stream<Character> pwdStream = Stream.concat(getRandomNumbers(2),
                Stream.concat(getRandomSpecialChars(2),
                        Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
        // Shuffle the characters and concatenate them to form the password
        List<Character> charList = pwdStream.collect(Collectors.toList());
        Collections.shuffle(charList);
        String password = charList.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

    /**
     * Generates a stream of random numbers.
     *
     * @param count Number of random numbers to generate.
     * @return Stream of random numbers.
     */
    private static Stream<Character> getRandomNumbers(int count) {
        IntStream numbers = random.ints(count, 48, 57);
        return numbers.mapToObj(data -> (char) data);
    }

    /**
     * Generates a stream of random special characters.
     *
     * @param count Number of random special characters to generate.
     * @return Stream of random special characters.
     */
    private static Stream<Character> getRandomSpecialChars(int count) {
        IntStream specialChars = random.ints(count, 33, 45);
        return specialChars.mapToObj(data -> (char) data);
    }

    /**
     * Generates a stream of random alphabets (either upper or lower case).
     *
     * @param count     Number of random alphabets to generate.
     * @param upperCase Indicates whether to generate uppercase alphabets.
     * @return Stream of random alphabets.
     */
    private static Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
        IntStream characters = null;
        if (upperCase) {
            characters = random.ints(count, 65, 90);
        } else {
            characters = random.ints(count, 97, 122);
        }
        return characters.mapToObj(data -> (char) data);
    }
}
