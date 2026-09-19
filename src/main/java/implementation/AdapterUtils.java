package implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.printscript.common.LanguageVersion;
import org.printscript.common.Position;
import org.printscript.common.ast.LazyProgram;
import org.printscript.common.ast.Program;
import org.printscript.common.token.TokenType;
import org.printscript.lexer.IdentifierTokenMatcher;
import org.printscript.lexer.LexerImpl;
import org.printscript.lexer.NumberTokenMatcher;
import org.printscript.lexer.StringTokenMatcher;
import org.printscript.lexer.SymbolTokenMatcher;
import org.printscript.lexer.TokenMatcher;
import org.printscript.parser.ParserImpl;

/** Shared utilities for TCK adapters. */
public class AdapterUtils {

    private AdapterUtils() {}

    /** Builds the full set of token matchers used across all pipeline adapters. */
    public static List<TokenMatcher> buildMatchers() {
        return List.of(
                new IdentifierTokenMatcher(
                        Map.ofEntries(
                                Map.entry("let", TokenType.LET),
                                Map.entry("const", TokenType.CONST),
                                Map.entry("if", TokenType.IF),
                                Map.entry("else", TokenType.ELSE),
                                Map.entry("number", TokenType.TYPE_NUMBER),
                                Map.entry("string", TokenType.TYPE_STRING),
                                Map.entry("boolean", TokenType.TYPE_BOOLEAN),
                                Map.entry("true", TokenType.BOOLEAN_LITERAL),
                                Map.entry("false", TokenType.BOOLEAN_LITERAL))),
                new NumberTokenMatcher(),
                new StringTokenMatcher(),
                new SymbolTokenMatcher(
                        Map.ofEntries(
                                Map.entry(':', TokenType.COLON),
                                Map.entry('=', TokenType.ASSIGN),
                                Map.entry(';', TokenType.SEMICOLON),
                                Map.entry('(', TokenType.LPAREN),
                                Map.entry(')', TokenType.RPAREN),
                                Map.entry('{', TokenType.LBRACE),
                                Map.entry('}', TokenType.RBRACE),
                                Map.entry('+', TokenType.PLUS),
                                Map.entry('-', TokenType.MINUS),
                                Map.entry('*', TokenType.STAR),
                                Map.entry('/', TokenType.SLASH))));
    }

    /** Parses the source stream into a lazy program according to the given language version. */
    public static Program buildProgram(InputStream src, String version) {
        LanguageVersion langVersion = LanguageVersion.fromLabel(version);
        Reader reader = new InputStreamReader(src, StandardCharsets.UTF_8);
        var lexer = new LexerImpl(reader, buildMatchers());
        var parser = new ParserImpl(lexer, langVersion);
        return new LazyProgram(parser, new Position(1, 1, 1, 1));
    }
}
