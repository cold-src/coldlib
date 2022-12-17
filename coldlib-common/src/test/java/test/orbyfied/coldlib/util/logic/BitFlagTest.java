package test.orbyfied.coldlib.util.logic;

import net.orbyfied.coldlib.util.logic.BitFlag;
import org.junit.jupiter.api.Test;

public class BitFlagTest {

    @Test
    void test_Set() {
        long i = 0;
        i = BitFlag.at(-1).set(i, true);
        System.out.println(Long.toBinaryString(i));
    }

}
