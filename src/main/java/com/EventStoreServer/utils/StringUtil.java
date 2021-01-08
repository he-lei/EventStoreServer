package com.EventStoreServer.utils;

import com.EventStoreServer.entity.LeafNode;
import com.EventStoreServer.entity.Node;
import com.EventStoreServer.entity.NonLeafNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 从磁盘中读取出的数据，转换为字符串，使用此方法截取根节点的地址并返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return 返回根节点的地址数据
     */
    public static Long getRootAddress(String s) {
        Matcher m = Pattern.compile("^\\d{1,10}").matcher(s);
        m.find();
        return Long.parseLong(m.group());
    }

    /**
     * 从字符串中截取保存当前节点数据个数的字符串，并将此字符串返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return 保存当前节点数据个数的字符串
     */
    public static Integer getNodeDateNumber(String s) {
        Matcher m = Pattern.compile("(?<=\\{)\\d*(?=;k)").matcher(s);
        m.find();
        String dataNumber = m.group();
        return Integer.parseInt(dataNumber);
    }

    /**
     * 从字符串中截取Key的数据，并将数据添加到字符串数组当中返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return Key的字符串数组
     */
    public static String[] getKeyArray(String s) {
        Matcher m1 = Pattern.compile("(?<=;key\\{\\().*(?=\\)\\};a|\\)\\};v)").matcher(s);
        m1.find();
        String s1 = m1.group();
        String[] strings = s1.split("\\)\\(");
        return strings;
    }

    /**
     * 从字符串中截取Value的数据，并将数据添加到字符串数组当中返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return Value的字符串数组
     */
    public static String[] getValueArray(String s) {
        Matcher m1 = Pattern.compile("(?<=;value\\{\\().*(?=\\)\\};F)").matcher(s);
        m1.find();
        String s1 = m1.group();
        String[] strings = s1.split("\\)\\(");
        return strings;
    }

    /**
     * 从字符串中截取子节点地址的数据，并将数据添加到字符串数组当中返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return Address的字符串数组
     */
    public static String[] getAddressArray(String s) {
        Matcher m1 = Pattern.compile("(?<=;address\\{\\().*(?=\\)\\};F)").matcher(s);
        m1.find();
        String s1 = m1.group();
        String[] strings = s1.split("\\)\\(");
        return strings;
    }

    /**
     * 从字符串中截取父节点所在位置的数据，并将数据返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return 父节点所在位置的地址数据
     */
    public static long getParentAddress(String s) {
        Matcher m = Pattern.compile("(?<=;F)\\d{1,10}(?=;R|;)").matcher(s);
        m.find();
        String parentAddress = m.group();
//        System.out.println(parentAddress);
        return Long.parseLong(parentAddress);
    }

    /**
     * 从字符串中截取右同级节点所在位置的数据，并将数据返回
     *
     * @param s 磁盘读取出的数据转化为的字符串
     * @return 右同级节点所在位置的地址数据
     */
    public static long getRightAddress(String s) {
        Matcher m = Pattern.compile("(?<=;R)\\d{1,10}(?=;)").matcher(s);
        m.find();
        String rightAddress = m.group();
//        System.out.println(rightAddress);
        return Long.parseLong(rightAddress);
    }

    /**
     * 从磁盘中读取出的全部数据转换为字符串后，需要进行过滤，从中筛选出数据字符串
     *
     * @param s 磁盘中读取出的全部字符串
     * @return 筛选后的数据字符串
     */
    public static String getDateString(String s) {
        Pattern p = Pattern.compile("data\\{.*\\}");
        Matcher m = p.matcher(s);
        m.find();
        return m.group();
    }


    // 此方法用来确认当前节点保存的是位置还是数据
    // 当前节点保存的是地址,则当前节点为非叶子节点,其子节点的类型需要取出数据字符串后再进行判断
    // 当前节点保存的是数据,则当前节点为叶子节点.
    public static Node getType(String s){
        Matcher m = Pattern.compile("(?<=\\)\\};)(address|value)(?=\\{\\()").matcher(s);
        m.find();
        if ("address".equals(m.group())) {
            return new NonLeafNode();
        }
        if ("value".equals(m.group())) {
            return new LeafNode();
        }
        return null;
    }

    public static String getArrayString(String[] strings, String s){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(s);
        stringBuffer.append("{(");
        for (int i = 0; i < strings.length; i++) {
            if (i == strings.length - 1){
                stringBuffer.append(strings[i]);
                stringBuffer.append(")}");
                break;
            }
            stringBuffer.append(strings[i]);
            stringBuffer.append(")(");
        }
        return stringBuffer.toString();
    }

    public static long getNodeAddress(String s){
        Matcher m = Pattern.compile("(?<=;)\\d{1,10}(?=\\})").matcher(s);
        m.find();
        return Long.parseLong(m.group());
    }

//    public static void main(String[] args) {
//        String string = "1234566890 data{2;N;key{(abcdefghigklmnopqrstuvwxy_12345678901234567890123456789012)(abcdefghigklmnopqrstuvwxy_12345678901234567890123456789012)};address{(1234567890)(1234567890)};F9876543210;}\n00000000000000000000000000000000" +
//                "data{2;L;key{(ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)(ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)};value{(987654321_ABCDEFGHIJKLMNOP_9876543210_87654321)(987654321_ABCDEFGHIJKLMNOP_9876543210_87654321)};F9876543210;R9876543210}000000000000000";
//        String s3 = "data{2;L;key{(ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)(ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)};value{(987654321_ABCDEFGHIJKLMNOP_9876543210_87654321)(987654321_ABCDEFGHIJKLMNOP_9876543210_87654321)};F9876543210;R9876543210}\n";
//        String dataString = getDateString(string);
//        System.out.println(dataString);
//        Long address = getRootAddress(string);
//        System.out.println(address);
//        String[] strings = getKeyArray(dataString);
//        for (String s : strings) {
//            System.out.println(s);
//        }
//        String[] strings2 = getValueArray(s3);
//        for (String s : strings2) {
//            System.out.println(s);
//        }
//        String[] strings3 = getAddressArray(dataString);
//        for (String s : strings3) {
//            System.out.println(s);
//        }
//        Integer integer = getNodeDateNumber(dataString);
//        System.out.println(integer);
//        Long integer1 = getParentAddress(dataString);
//        System.out.println(integer1);
//        Long l = getRightAddress(s3);
//        System.out.println(l);
//    }
//
//    public static void main(String[] args) {
//        String[] strs = {"asfasasc","sdasdca","sdasc aasad","asdxnascf"};
//        String string = getArrayString(strs,"key");
//        System.out.println(string);
//    }

}
