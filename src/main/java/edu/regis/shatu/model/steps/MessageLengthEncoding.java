package edu.regis.shatu.model.steps;

/** Binary representations of the original message length for SHA-256 practice. */
public final class MessageLengthEncoding {
    public static final int MAX_CHARACTERS = 1024;

    private MessageLengthEncoding() {
    }

    public static void validateLength(int characters) {
        if (characters < 1 || characters > MAX_CHARACTERS) {
            throw new IllegalArgumentException("Enter a message length between 1 and " + MAX_CHARACTERS + ".");
        }
    }

    /** The practice screen asks for binary without leading zeros. */
    public static String practiceAnswer(int characters) {
        validateLength(characters);
        return Long.toBinaryString((long) characters * 8);
    }

    /** The field appended to the padded message is exactly 64 bits. */
    public static String lengthBits64(int characters) {
        String answer = practiceAnswer(characters);
        return "0".repeat(64 - answer.length()) + answer;
    }
}
