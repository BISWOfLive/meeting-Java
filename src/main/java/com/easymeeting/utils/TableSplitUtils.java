package com.easymeeting.utils;

import java.nio.charset.StandardCharsets;

public class TableSplitUtils {
    private static final String SPLIT_TABLE_MEETING_CHAT_MESSAGE = "meeting_chat_message";

    private static final String CREATE_TABLE_TEMP = "CREATE TABLE IF NOT EXISTS %s like %s;";

    private static final Integer SPLIT_TABLE_COUNT = 32;

    /**
     * 生成创建分表的SQL语句
     * 根据模板表名、表索引和总表数生成带编号的新表名，并替换CREATE_TABLE_TEMP模板中的占位符
     * @param templateTableName 模板表名
     * @param tableIndex 表索引，用于生成表名后缀
     * @param tableCount 总表数量，用于确定表名编号的填充长度
     * @return 替换占位符后的创建表SQL语句
     */
    public static String getCreateTableSql(String templateTableName, Integer tableIndex, Integer tableCount) {
        Integer padLen = String.valueOf(tableCount).length();
        String tableName = templateTableName + "_" + String.format("%0" + padLen + "d", tableIndex);
        return String.format(CREATE_TABLE_TEMP, tableName, templateTableName);
    }

    public static String getMeetingChatMessageTable(String meetingId){
        return getTableName(SPLIT_TABLE_MEETING_CHAT_MESSAGE,SPLIT_TABLE_COUNT,meetingId);
    }
    /**
     * 根据分表策略生成表名
     * 使用MurmurHash算法计算key的哈希值，然后通过取模运算确定表序号，
     * 最终生成带序号后缀的表名
     * @param prefix 表名前缀
     * @param tableCount 分表总数
     * @param key 用于分表计算的键值
     * @return 带序号后缀的分表名
     */
    private static String getTableName(String prefix, Integer tableCount, String key) {
        int hashCode = Math.abs(murmurHash(key));
        int tableNum = hashCode % tableCount + 1;
        int tableNumLength = String.valueOf(tableCount).length();
        return prefix + "_" + String.format("%0" + tableNumLength + "d",tableNum);
    }

    private static void getSplitTableSQL() {
        for (int i = 1; i <= SPLIT_TABLE_COUNT; i++) {
            System.out.println(getCreateTableSql(SPLIT_TABLE_MEETING_CHAT_MESSAGE, i, SPLIT_TABLE_COUNT));
        }
    }

//    public static void main(String[] args) {
//        getSplitTableSQL();
//    }

    private static int murmurHash(String key) {
        final byte[] data = key.getBytes(StandardCharsets.UTF_8);
        final int length = data.length;
        final int seed = 0x9747b28c;
        final int m = 0x5bd1e995;
        final int r = 24;

        int h = seed ^ length;
        int len_4 = length >> 2;

        for (int i = 0; i < len_4; i++) {
            int i_4 = i << 2;
            int k = data[i_4 + 3];
            k = k << 8;
            k = k | (data[i_4 + 2] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 1] & 0xff);
            k = k << 8;
            k = k | (data[i_4 + 0] & 0xff);
            k *= m;
            k ^= k >>> r;
            k *= m;
            h *= m;
            h ^= k;
        }

        int len_m = len_4 << 2;
        int left = length - len_m;

        if (left != 0) {
            if (left >= 3) h ^= (data[length - 3] & 0xff) << 16;
            if (left >= 2) h ^= (data[length - 2] & 0xff) << 8;
            if (left >= 1) h ^= (data[length - 1] & 0xff);
            h *= m;
        }

        h ^= h >>> 13;
        h *= m;
        h ^= h >>> 15;

        return h;
    }
}
