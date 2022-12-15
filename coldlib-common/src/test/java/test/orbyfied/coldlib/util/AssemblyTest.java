package test.orbyfied.coldlib.util;

import net.orbyfied.coldlib.util.Assembly;
import org.junit.jupiter.api.Test;

public class AssemblyTest {

    record StringAssemblyOptions(String name, boolean prefix, int repeat) { }

    @Test
    void test_BasicAssembly() {
        var assembly = Assembly.builder(StringBuilder.class, String.class,
                StringAssemblyOptions.class)
                .setInstanceFactory(options -> new StringBuilder(options.prefix ? "cunt > " : ""))
                .addIntermediateTransformer((b, o) -> b.append("Hello, " + o.name))
                .setFinalizer(StringBuilder::toString)
                .addResultTransformer((in, options) -> in.repeat(options.repeat))
                .build();

        System.out.println(assembly.build(new StringAssemblyOptions("Orbyfied", true, 10)));
    }

}
