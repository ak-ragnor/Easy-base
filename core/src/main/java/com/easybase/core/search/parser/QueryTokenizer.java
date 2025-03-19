package com.easybase.core.search.parser;

import com.easybase.core.search.exception.SearchException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokenizes an OData filter expression into a list of tokens.
 */
public class QueryTokenizer {
    // Token types
    public static final int TOKEN_LPAREN = 1;
    public static final int TOKEN_RPAREN = 2;
    public static final int TOKEN_COMMA = 3;
    public static final int TOKEN_LOGICAL_OP = 4;
    public static final int TOKEN_COMPARISON_OP = 5;
    public static final int TOKEN_FUNCTION = 6;
    public static final int TOKEN_IDENTIFIER = 7;
    public static final int TOKEN_STRING = 8;
    public static final int TOKEN_NUMBER = 9;
    public static final int TOKEN_BOOLEAN = 10;
    public static final int TOKEN_NULL = 11;

    // Token patterns
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\s+");
    private static final Pattern PATTERN_LPAREN = Pattern.compile("^\\(");
    private static final Pattern PATTERN_RPAREN = Pattern.compile("^\\)");
    private static final Pattern PATTERN_COMMA = Pattern.compile("^,");
    private static final Pattern PATTERN_LOGICAL_OP = Pattern.compile("^(and|or|not)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_COMPARISON_OP = Pattern.compile("^(eq|ne|gt|ge|lt|le|startsWith)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_FUNCTION = Pattern.compile("^(contains)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_IDENTIFIER = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_.]*");
    private static final Pattern PATTERN_STRING = Pattern.compile("^'([^']*)'");
    private static final Pattern PATTERN_NUMBER = Pattern.compile("^-?\\d+(\\.\\d+)?");
    private static final Pattern PATTERN_BOOLEAN = Pattern.compile("^(true|false)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern PATTERN_NULL = Pattern.compile("^null\\b", Pattern.CASE_INSENSITIVE);

    /**
     * Token class representing a single token in the input.
     */
    public static class Token {
        private final int type;
        private final String value;
        private final int position;

        public Token(int type, String value, int position) {
            this.type = type;
            this.value = value;
            this.position = position;
        }

        public int getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        public int getPosition() {
            return position;
        }

        @Override
        public String toString() {
            String typeName = switch (type) {
                case TOKEN_LPAREN -> "LPAREN";
                case TOKEN_RPAREN -> "RPAREN";
                case TOKEN_COMMA -> "COMMA";
                case TOKEN_LOGICAL_OP -> "LOGICAL_OP";
                case TOKEN_COMPARISON_OP -> "COMPARISON_OP";
                case TOKEN_FUNCTION -> "FUNCTION";
                case TOKEN_IDENTIFIER -> "IDENTIFIER";
                case TOKEN_STRING -> "STRING";
                case TOKEN_NUMBER -> "NUMBER";
                case TOKEN_BOOLEAN -> "BOOLEAN";
                case TOKEN_NULL -> "NULL";
                default -> "UNKNOWN";
            };
            return String.format("%s(%s)@%d", typeName, value, position);
        }
    }

    /**
     * Tokenizes the input string into a list of tokens.
     *
     * @param input The input filter expression string
     * @return A list of tokens
     * @throws SearchException If the input cannot be tokenized
     */
    public List<Token> tokenize(String input) throws SearchException {
        List<Token> tokens = new ArrayList<>();
        String remaining = input;
        int position = 0;

        while (!remaining.isEmpty()) {
            // Skip whitespace
            Matcher whitespaceMatcher = PATTERN_WHITESPACE.matcher(remaining);
            if (whitespaceMatcher.find()) {
                String whitespace = whitespaceMatcher.group();
                remaining = remaining.substring(whitespace.length());
                position += whitespace.length();
                continue;
            }

            // Try to match tokens
            Token token = _matchToken(remaining, position);
            if (token == null) {
                throw new SearchException("Unexpected character at position " + position + ": " + remaining);
            }

            tokens.add(token);
            remaining = remaining.substring(token.getValue().length());
            position += token.getValue().length();
        }

        return tokens;
    }

    /**
     * Matches a token at the beginning of the input string.
     *
     * @param input The input string
     * @param position The current position in the original input
     * @return The matched token, or null if no match
     */
    private Token _matchToken(String input, int position) {
        // Try to match each token type
        Matcher matcher;

        // Left parenthesis
        matcher = PATTERN_LPAREN.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_LPAREN, matcher.group(), position);
        }

        // Right parenthesis
        matcher = PATTERN_RPAREN.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_RPAREN, matcher.group(), position);
        }

        // Comma
        matcher = PATTERN_COMMA.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_COMMA, matcher.group(), position);
        }

        // Logical operator
        matcher = PATTERN_LOGICAL_OP.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_LOGICAL_OP, matcher.group().toLowerCase(), position);
        }

        // Comparison operator
        matcher = PATTERN_COMPARISON_OP.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_COMPARISON_OP, matcher.group().toLowerCase(), position);
        }

        // Function
        matcher = PATTERN_FUNCTION.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_FUNCTION, matcher.group().toLowerCase(), position);
        }

        // Identifier
        matcher = PATTERN_IDENTIFIER.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_IDENTIFIER, matcher.group(), position);
        }

        // String
        matcher = PATTERN_STRING.matcher(input);
        if (matcher.find()) {
            // Return the whole match including quotes
            return new Token(TOKEN_STRING, matcher.group(), position);
        }

        // Number
        matcher = PATTERN_NUMBER.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_NUMBER, matcher.group(), position);
        }

        // Boolean
        matcher = PATTERN_BOOLEAN.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_BOOLEAN, matcher.group().toLowerCase(), position);
        }

        // Null
        matcher = PATTERN_NULL.matcher(input);
        if (matcher.find()) {
            return new Token(TOKEN_NULL, matcher.group().toLowerCase(), position);
        }

        return null;
    }
}