package implementation;

import static implementation.AdapterUtils.SERVICES;
import static implementation.AdapterUtils.reader;
import static implementation.AdapterUtils.version;

import interpreter.ErrorHandler;
import interpreter.PrintScriptLinter;
import java.io.InputStream;
import java.util.List;
import org.printscript.analyzer.violation.Violation;
import org.printscript.rules.RuleConfig;

public class PrintScriptLinterAdapter implements PrintScriptLinter {

    @Override
    public void lint(InputStream src, String version, InputStream config, ErrorHandler handler) {
        try {
            RuleConfig rules = RuleConfig.fromJson(reader(config));
            // The analysis also runs the semantic check: a type error is reported as an error too.
            List<Violation> violations =
                    SERVICES.analysis().analyze(reader(src), version(version), rules);
            for (Violation violation : violations) {
                handler.reportError(violation.toString());
            }
        } catch (Exception e) {
            handler.reportError(e.getMessage());
        }
    }
}
