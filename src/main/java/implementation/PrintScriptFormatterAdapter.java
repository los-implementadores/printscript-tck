package implementation;

import static implementation.AdapterUtils.SERVICES;
import static implementation.AdapterUtils.reader;
import static implementation.AdapterUtils.version;

import interpreter.PrintScriptFormatter;
import java.io.InputStream;
import java.io.Writer;
import org.printscript.rules.RuleConfig;

public class PrintScriptFormatterAdapter implements PrintScriptFormatter {

    @Override
    public void format(InputStream src, String version, InputStream config, Writer writer) {
        try {
            RuleConfig rules = RuleConfig.fromJson(reader(config));
            SERVICES.formatting().format(reader(src), version(version), rules, writer);
        } catch (Exception e) {
            throw new RuntimeException("Formatter error: " + e.getMessage(), e);
        }
    }
}
