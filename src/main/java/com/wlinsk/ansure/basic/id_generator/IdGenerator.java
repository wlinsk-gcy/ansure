package com.wlinsk.ansure.basic.id_generator;

/**
 * twitter id生成算法snowflake
 *
 * @Author: wlinsk
 * @Date: 2024/5/22
 */
public class IdGenerator {
    //10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId
    private long workerId;
    private long datacenterId;

    //同一个时间截内生成的序列数，初始值是0，从0开始
    private long sequence = 1L;

    //    private long twepoch = 1288834974657L;//2010-11-04 09:42:54
    private long twepoch = 1716386747161L;//2024-05-22 22:05:47
    //机器id所占的位数
    private long workerIdBits = 5L;
    //数据标识id所占的位数
    private long datacenterIdBits = 5L;
    //支持的最大机器id，结果是31,这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数
    private long maxWorkerId = -1L ^ (-1L << workerIdBits);
    //支持的最大数据标识id
    private long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    //序列在id中占的位数
    private long sequenceBits = 11L;

    private long flagBits = 1L;

    private long flagIdShift = sequenceBits;
    //机器id向左移12位
    private long workerIdShift = sequenceBits + flagBits;
    //数据标识id向左移17位
    private long datacenterIdShift = sequenceBits + workerIdBits + flagBits;
    /// /时间截向左移5+5+11+1=22位
    private long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits + flagBits;
    //生成序列的掩码，这里为1111 1111 1111
    private long sequenceMask = -1L ^ (-1L << sequenceBits);

    //时间回拨标记 默认是0
    private long flagSequence = 0L;
    //上次生成id的时间截
    private long lastTimestamp = -1L;

    private long lastCallbackTime = -1L;

    public IdGenerator(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }

        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized String nextSeq() {
        return String.valueOf(nextId());
    }

    private synchronized long nextId() {
        long timestamp = timeGen();

        flagSequence = 0L;
        if (timestamp < lastTimestamp) {
            // 如果最后一次回拨时间小于上次更新时间,则更新最后一次回拨时间
            /**
             * 时间服务器 10:10
             * 服务器回拨到10:10
             * 最后一次回拨时间(产生订单号) 10:10
             */
            if (lastCallbackTime < lastTimestamp) {
                lastCallbackTime = lastTimestamp;
            }
        }
        if (timestamp <= lastCallbackTime) {
            flagSequence = 1L;
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        /**
         * 返回结果：
         * (timestamp - twepoch) << timestampLeftShift) 表示将时间戳减去初始时间戳，再左移相应位数
         * (datacenterId << datacenterIdShift) 表示将数据id左移相应位数
         * (workerId << workerIdShift) 表示将工作id左移相应位数
         * | 是按位或运算符，例如：x | y，只有当x，y都为0的时候结果才为0，其它情况结果都为1。
         * 因为个部分只有相应位上的值有意义，其它位上都是0，所以将各部分的值进行 | 运算就能得到最终拼接好的id
         */
        return ((timestamp - twepoch) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | (flagSequence << flagIdShift) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}
