package org.interpreters.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.interpreters.lox.TokenType.*;

interface ScannerInterface {
  public List<Token> scanTokens();

  char NULL_TERMINATOR = '\0';
}

public class Scanner implements ScannerInterface {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      start = current;
      scanToken();
    }
    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Helper method incharge of taking in characters
   * and identifying their tokens.
   * 
   * @returns void
   */
  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        if (match('*')) {
          addToken(STAR_STAR);
        } else {
          addToken(STAR);
        }
        break;
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
      // We don't need to add comments to be parsed.
      case '/':
        // this if case checks for either single
        // or multiline comments
        if (match('/') || match('*')) {
          while (peek() != '\n' && !isAtEnd())
            advance();
        } else {
          addToken(SLASH);
        }
        break;
      case ' ':
      case '\r':
      case '\t':
        break;
      case '\n':
        line++;
        break;
      case '"':
        string();
        break;
      default:
        Lox.error(line, "Unexpected Character.");
        break;

    }
  }

  /**
   * Primarily in charge of two things.
   * The first is increase the current pointer by one only after
   * reading in the character.
   * 
   * @exception Any exception
   * @return Char
   */
  private char advance() {
    return source.charAt(current++);
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n')
        line++;
      advance();
    }
    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }
    advance();
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private boolean match(char expected) {
    // used to match into the next character after getting a hold
    // of the current one.

    if (isAtEnd())
      return false;
    if (source.charAt(current) != expected)
      return false;
    current++;
    return true;
  }

  /**
   * Custom private method that return the [current] idx
   * element. If it reaches the end of a stream or data,
   * It will return the null termination character sequence.
   * In this case, "\0"
   * 
   * @return String
   */
  private char peek() {
    if (isAtEnd())
      return NULL_TERMINATOR;
    return source.charAt(current);
  }
}
