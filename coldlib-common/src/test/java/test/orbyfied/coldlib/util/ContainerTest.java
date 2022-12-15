package test.orbyfied.coldlib.util;

import net.orbyfied.coldlib.util.Assembly;
import net.orbyfied.coldlib.util.Container;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.HashMap;
import java.util.Map;

public class ContainerTest {

    //
    // Test: Protected
    //

    @Test
    void test_Protected() {
        // create normal container
        final Container<String> container = Container.mutable("Hello, World!");
        Assertions.assertEquals("Hello, World!", container.get());

        // protect container
        final Container<String> protectedContainer = Container.asProtected(
                container,
                element -> element.getMethodName().equals("test_Protected"),
                false
        );

        protectedContainer.get();
        test_Protected_shouldThrow(protectedContainer);
    }

    void test_Protected_shouldThrow(final Container<String> protectedContainer) {

        Assertions.assertThrows(SecurityException.class, protectedContainer::get);
    }

}
