package com.wlinsk.ansure.basic.id_generator;

import org.apache.commons.lang3.RandomUtils;

public class IdUtils {

    private final static IdGenerator sequenceGenerator =
            new IdGenerator(RandomUtils.nextInt(1, 10), RandomUtils.nextInt(1, 10));

    public static String build(String prefix) {
        String id = sequenceGenerator.nextSeq();
        return prefix == null ? id : prefix + id;
    }
}
