package test.orbyfied.coldlib.util;

import net.orbyfied.coldlib.util.Container;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ContainerTest {

    //
    // Test: Chained
    //

    @Test
    void test_ModChain() {
        Container<Integer> integerContainer =
                Container.atomic();
        Container<String>  stringContainer  =
                integerContainer.biMap(String::valueOf, Integer::parseInt);

        System.out.println(integerContainer.get());
        stringContainer.set("69");
        System.out.println(integerContainer.get());
    }

    //
    // Test: Protected
    //

    @Test
    void test_Protected() {
        // create normal container
        final Container<String> container = Container.mutable("Hello, World!");
        Assertions.assertEquals("Hello, World!", container.get());

        // protect container
        final Container<String> protectedContainer = Container.protect(
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
