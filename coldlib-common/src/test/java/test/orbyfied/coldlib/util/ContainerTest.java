package test.orbyfied.coldlib.util;

import net.orbyfied.coldlib.util.Container;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class ContainerTest {

    //
    // Test: Protected
    //

    @Test
    void test_Protected() {
        // create normal container
        Container<String> container = Container.mutable("Hello, World!");
        Assertions.assertEquals("Hello, World!", container.get());

        // protect container
        Container<String> protectedContainer = Container.asProtected(
                container,
                element -> {
                    return element.getMethodName().equals("test_Protected");
                },
                false
        );
        protectedContainer.get();
        test_Protected_shouldThrow(protectedContainer);
    }

    void test_Protected_shouldThrow(Container<String> protectedContainer) {
        Assertions.assertThrows(SecurityException.class, protectedContainer::get);
    }

}
