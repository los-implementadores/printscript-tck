package implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.printscript.common.LanguageVersion;
import org.printscript.common.Position;
import org.printscript.common.ast.LazyProgram;
import org.printscript.common.ast.Program;
import org.printscript.lexer.LexerFactory;
import org.printscript.parser.ParserImpl;

/** Shared utilities for TCK adapters. */
public class AdapterUtils {

    private AdapterUtils() {}

    /** Parses the source stream into a lazy program according to the given language version. */
    public static Program buildProgram(InputStream src, String version) {
        LanguageVersion langVersion = LanguageVersion.fromLabel(version);
        var lexer = LexerFactory.create(new InputStreamReader(src, StandardCharsets.UTF_8));
        var parser = new ParserImpl(lexer, langVersion);
        return new LazyProgram(parser, new Position(1, 1, 1, 1));
    }
}
