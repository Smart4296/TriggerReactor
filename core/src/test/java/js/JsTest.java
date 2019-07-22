package js;

import io.github.wysohn.triggerreactor.core.manager.AbstractExecutorManager;
import io.github.wysohn.triggerreactor.core.manager.AbstractPlaceholderManager;

import javax.script.ScriptEngine;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public abstract class JsTest {
    protected static final Path resourcePath = Paths.get("src", "main", "resources");

    protected final String name;
    protected final File file;
    protected Map<String, Object> varMap = new HashMap<>();
    protected Object[] args;

    private JsTest(String name, File file){
        this.name = name;
        this.file = file;
    }

    public abstract Object test(ScriptEngine engine) throws Exception;

    public static class ExecutorTest extends JsTest{
        public ExecutorTest(String name, File file) {
            super(name, file);
        }

        @Override
        public Object test(ScriptEngine engine) throws Exception {
            AbstractExecutorManager.JSExecutor exec =
                    new AbstractExecutorManager.JSExecutor(name, engine, file);
            exec.execute(true, varMap, null, args);
            return null;
        }
    }

    public static class PlaceholderTest extends JsTest{
        public PlaceholderTest(String name, File file) {
            super(name, file);
        }

        @Override
        public Object test(ScriptEngine engine) throws Exception {
            AbstractPlaceholderManager.JSPlaceholder ph =
                    new AbstractPlaceholderManager.JSPlaceholder(name, engine, file);
            return ph.parse(null, varMap, args);
        }
    }

    public static class JsTester<TEST extends JsTest>{
        private final TEST test;

        public JsTester(TEST test) {
            this.test = test;
        }

        public JsTester<TEST> addVariable(String key, Object value){
            test.varMap.put(key, value);
            return this;
        }

        public JsTester<TEST> withArgs(Object... args){
            test.args = args;
            return this;
        }

        public Object test(ScriptEngine engine) throws Exception{
            return test.test(engine);
        }

        public static JsTester<ExecutorTest> executorTestOf(String name){
            Path basePath = Paths.get(resourcePath.toString(), "Executor");
            File file = Paths.get(basePath.toString(), name+".js").toFile();
            ExecutorTest test = new ExecutorTest(name, file);
            return new JsTester<>(test);
        }

        public static JsTester<PlaceholderTest> placeholderTestOf(String name){
            Path basePath = Paths.get(resourcePath.toString(), "Placeholder");
            File file = Paths.get(basePath.toString(), name+".js").toFile();
            PlaceholderTest test = new PlaceholderTest(name, file);
            return new JsTester<>(test);
        }
    }
}
