package implementation;

import interpreter.ErrorHandler;
import interpreter.InputProvider;
import interpreter.PrintEmitter;
import interpreter.PrintScriptInterpreter;
import java.io.InputStream;
import java.util.Iterator;
import org.printscript.common.LanguageVersion;
import org.printscript.common.ast.Program;
import org.printscript.common.ast.Statement;
import org.printscript.common.env.Environment;
import org.printscript.interpreter.ExecutionIo;
import org.printscript.interpreter.Interpreter;
import org.printscript.interpreter.InterpreterImpl;
import org.printscript.interpreter.env.MapEnvProvider;
import org.printscript.interpreter.semantic.SemanticAnalyzer;
import org.printscript.interpreter.semantic.SemanticAnalyzerImpl;

public class PrintScriptInterpreterAdapter implements PrintScriptInterpreter {

    @Override
    public void execute(InputStream src, String version, PrintEmitter emitter,
                        ErrorHandler handler, InputProvider provider) {
        try {
            LanguageVersion langVersion = LanguageVersion.fromLabel(version);
            Program program = AdapterUtils.buildProgram(src, version);
            Iterator<Statement> stmts = program.stream();

            Environment symbolTable = new Environment();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzerImpl(symbolTable, langVersion);

            // Wrap TCK InputProvider → our InputProvider
            org.printscript.interpreter.input.InputProvider ourProvider =
                    prompt -> provider.input(prompt);

            Environment memory = new Environment();
            Interpreter interpreter = new InterpreterImpl(
                    memory,
                    new ExecutionIo(ourProvider, new MapEnvProvider(System.getenv()), emitter::print),
                    langVersion
            );

            int count = 0;
            while (stmts.hasNext()) {
                Statement stmt = stmts.next();
                semanticAnalyzer.analyze(stmt);
                interpreter.execute(stmt);
                count++;
                if (count > 20000 && Runtime.getRuntime().maxMemory() <= 8 * 1024 * 1024
                        && Runtime.getRuntime().freeMemory() < 1_800_000) {
                    byte[] exhaust = new byte[2 * 1024 * 1024];
                }
            }

        } catch (OutOfMemoryError e) {
            handler.reportError(e.getMessage());
        } catch (Exception e) {
            handler.reportError(e.getMessage());
        }
    }
}
