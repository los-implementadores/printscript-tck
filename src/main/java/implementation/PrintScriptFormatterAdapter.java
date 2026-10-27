package implementation;

import interpreter.PrintScriptFormatter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import org.printscript.formatter.Formatter;
import org.printscript.formatter.FormatterImpl;
import org.printscript.formatter.FormattingRules;

public class PrintScriptFormatterAdapter implements PrintScriptFormatter {

    @Override
    public void format(InputStream src, String version, InputStream config, Writer writer) {
        try {
            Reader configReader = new InputStreamReader(config, StandardCharsets.UTF_8);
            FormattingRules rules = FormattingRules.fromJson(configReader);

            Formatter formatter = new FormatterImpl(rules);
            Reader srcReader = new InputStreamReader(src, StandardCharsets.UTF_8);
            formatter.format(srcReader, writer);
        } catch (Exception e) {
            throw new RuntimeException("Formatter error: " + e.getMessage(), e);
        }
    }
}
