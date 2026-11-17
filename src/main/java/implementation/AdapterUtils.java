package implementation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import org.printscript.runner.LanguageServices;
import org.printscript.runner.LanguageVersion;

/**
 * Shared utilities for TCK adapters. The adapters go through PrintScript's runner, which builds
 * the lexer, parser and tools for the requested language version (the modules themselves do not
 * know about versions).
 */
public class AdapterUtils {

    /** PrintScript's language services: validation, analysis, execution and formatting. */
    static final LanguageServices SERVICES = LanguageServices.printScript();

    private AdapterUtils() {}

    /** Reads a TCK input stream as UTF-8. */
    static Reader reader(InputStream in) {
        return new InputStreamReader(in, StandardCharsets.UTF_8);
    }

    /** The language version for a TCK version label ("1.0" or "1.1"). */
    static LanguageVersion version(String label) {
        return LanguageVersion.fromLabel(label);
    }
}
