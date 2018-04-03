package org.esdemo.etc;

import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import org.esdemo.util.SimpleLogger;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-04-03
 * @GitHub : https://github.com/zacscoding
 */
public class AtomicLongTest {
    @Test
    public void test() {
        AtomicLong executionIdGen = new AtomicLong();
        IntStream.range(1, 5).forEach(i -> {
            long gen = executionIdGen.incrementAndGet();
            SimpleLogger.println("index : {}, gen : {}", i , gen);
        });

        IntStream.range(1, 5).forEach(i -> {
            long gen = executionIdGen.incrementAndGet();
            SimpleLogger.println("index : {}, gen : {}", i , gen);
        });
    }

}
