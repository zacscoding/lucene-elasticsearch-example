package org.esdemo.etc;

import org.esdemo.AbstractTestRunner;
import org.esdemo.entity.temp;
import org.junit.Test;

/**
 * @author zacconding
 * @Date 2018-04-09
 * @GitHub : https://github.com/zacscoding
 */
public class MappingTest  extends AbstractTestRunner {

    @Test
    public void setUp() {
        super.clearIndex(temp.class);
    }

}
