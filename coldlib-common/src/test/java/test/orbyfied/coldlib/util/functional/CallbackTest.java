package test.orbyfied.coldlib.util.functional;

import net.orbyfied.coldlib.util.functional.Callback;
import org.junit.jupiter.api.Test;

public class CallbackTest {

    @Test
    void testA() {
        Callback<String> callback = Callback.multi();
        callback.then(s -> System.out.println("1: " + s));
        callback.then(s -> System.out.println("2: " + s));
        callback.await().whenComplete((s, throwable) -> System.out.println("3: " + s));
        callback.call("your mom is gay");
        callback.call();
    }

}
