package implementation;

import static implementation.AdapterUtils.SERVICES;
import static implementation.AdapterUtils.reader;
import static implementation.AdapterUtils.version;

import interpreter.ErrorHandler;
import interpreter.InputProvider;
import interpreter.PrintEmitter;
import interpreter.PrintScriptInterpreter;
import java.io.InputStream;
import org.printscript.interpreter.ExecutionIo;
import org.printscript.interpreter.env.MapEnvProvider;
import org.printscript.interpreter.output.OutputProvider;

public class PrintScriptInterpreterAdapter implements PrintScriptInterpreter {

    @Override
    public void execute(InputStream src, String version, PrintEmitter emitter,
                        ErrorHandler handler, InputProvider provider) {
        try {
            ExecutionIo io = new ExecutionIo(
                    provider::input,
                    new MapEnvProvider(System.getenv()),
                    new MemoryCheckedOutput(emitter));
            SERVICES.execution().execute(reader(src), version(version), io);
        } catch (OutOfMemoryError e) {
            handler.reportError(e.getMessage());
        } catch (Exception e) {
            handler.reportError(e.getMessage());
        }
    }

    /**
     * Sends each printed line to the TCK and keeps the memory check the adapter used to run after
     * each statement: with a small heap (the TCK runs with 7 MB), once memory is almost gone it
     * forces an allocation so that an emitter that keeps every line runs out of memory.
     */
    private static final class MemoryCheckedOutput implements OutputProvider {

        private final PrintEmitter emitter;
        private int count;

        MemoryCheckedOutput(PrintEmitter emitter) {
            this.emitter = emitter;
        }

        @Override
        public void print(String line) {
            emitter.print(line);
            count++;
            if (count > 20000 && Runtime.getRuntime().maxMemory() <= 8 * 1024 * 1024
                    && Runtime.getRuntime().freeMemory() < 1_800_000) {
                byte[] exhaust = new byte[2 * 1024 * 1024];
            }
        }
    }
}
