package implementation;

import interpreter.ErrorHandler;
import interpreter.PrintScriptLinter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import org.printscript.analyzer.StaticAnalyzer;
import org.printscript.analyzer.StaticAnalyzerImpl;
import org.printscript.common.ast.Program;
import org.printscript.common.ast.Statement;
import org.printscript.common.configs.AnalyzerConfig;
import org.printscript.common.linterViolations.Violation;

public class PrintScriptLinterAdapter implements PrintScriptLinter {

    @Override
    public void lint(InputStream src, String version, InputStream config, ErrorHandler handler) {
        try {
            Program program = AdapterUtils.buildProgram(src, version);
            Iterator<Statement> stmts = program.stream();

            Reader configReader = new InputStreamReader(config, StandardCharsets.UTF_8);
            AnalyzerConfig analyzerConfig = AnalyzerConfig.fromJson(configReader);

            StaticAnalyzer analyzer = new StaticAnalyzerImpl(analyzerConfig);
            while (stmts.hasNext()) {
                analyzer.analyze(stmts.next());
            }

            for (Violation violation : analyzer.getViolations()) {
                handler.reportError(violation.toString());
            }

        } catch (Exception e) {
            handler.reportError(e.getMessage());
        }
    }
}
