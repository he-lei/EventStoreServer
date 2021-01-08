package com.EventStoreServer.utils;

import com.EventStoreServer.entity.LeafNode;
import com.EventStoreServer.entity.Node;
import com.EventStoreServer.entity.NonLeafNode;

import java.io.IOException;
import java.io.RandomAccessFile;

public class AccessUtil {
    private static final String indexFileAddress = "D:\\IDEAwork\\EventStoreServer\\src\\main\\resources\\index.ser";
    // 每次存取的数据量为1024字节
    public static Integer dataBytes = 1024;

    private static byte[] empty = new byte[dataBytes];

    private static byte[] data = new byte[dataBytes];


    public static long getRootAddress() {
        long rootAddress = 0;
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "r")) {
            raf.seek(0);
//            clearByte(data);
            raf.read(data);
            rootAddress = StringUtil.getRootAddress(new String(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootAddress;
    }

    /**
     * 获取根节点的方法
     *
     * @return 根节点对象（叶子节点类型返回叶子节类型，非叶子节点类型返回非叶子类型）
     */
    public static Node getRootNode() {
        Node node = null;
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "r")) {
            // 将文件指针移到文件头位置
            raf.seek(0);
            // 读取1024字节的数据到bytes数组中，因为在添加数据时已预留了空间在文件中，所以不存在读取不到数据的情况
//            clearByte(data);
            raf.read(data);
            // 获取根节点所在位置，并将文件指针指向该位置
            raf.seek(StringUtil.getRootAddress(new String(data)));
//            clearByte(data);
            // 再次读取1024字节
            raf.read(data);
            // 获取数据字符串
            String dataString = StringUtil.getDateString(new String(data));
            // 判断根节点的类型（即value中保存的是数据还是子节点的地址）
            node = StringUtil.getType(dataString);
            if (node instanceof LeafNode) {
                LeafNode rootNode = (LeafNode) node;
                rootNode.setNumber(StringUtil.getNodeDateNumber(dataString));
                rootNode.setKey(StringUtil.getKeyArray(dataString));
                rootNode.setValue(StringUtil.getValueArray(dataString));
                rootNode.setNodeAddress(StringUtil.getNodeAddress(dataString));
                return rootNode;
            } else {
                NonLeafNode rootNode = (NonLeafNode) node;
                rootNode.setNumber(StringUtil.getNodeDateNumber(dataString));
                rootNode.setKey(StringUtil.getKeyArray(dataString));
                rootNode.setValue(StringUtil.getAddressArray(dataString));
                rootNode.setNodeAddress(StringUtil.getNodeAddress(dataString));
                return rootNode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

//    /**
//     * 根据地址，读取到保存数据的字符串
//     *
//     * @param address 数据所在地址
//     * @return 数据字符串
//     */
//    public  String getNodeString(long address) {
//        String dataString = null;
//        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "r")) {
//            raf.seek(address);
//            raf.read(databytes);
//            dataString = stringUtil.getDateString(new String(databytes));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return dataString;
//    }

    public static Node getNode(long address) {
        Node node = null;
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "r")) {
            raf.seek(address);
//            clearByte(data);
            raf.read(data);
            String dataString = StringUtil.getDateString(new String(data));
            node = StringUtil.getType(dataString);
            if (node instanceof LeafNode) {
                LeafNode leafNode = (LeafNode) node;
                leafNode.setNumber(StringUtil.getNodeDateNumber(dataString));
                leafNode.setKey(StringUtil.getKeyArray(dataString));
                leafNode.setValue(StringUtil.getValueArray(dataString));
                leafNode.setParentNodeAddress(StringUtil.getParentAddress(dataString));
                leafNode.setRightNodeAddress(StringUtil.getRightAddress(dataString));
                leafNode.setNodeAddress(StringUtil.getNodeAddress(dataString));
                return leafNode;
            } else {
                NonLeafNode nonLeafNode = (NonLeafNode) node;
                nonLeafNode.setNumber(StringUtil.getNodeDateNumber(dataString));
                nonLeafNode.setKey(StringUtil.getKeyArray(dataString));
                nonLeafNode.setValue(StringUtil.getAddressArray(dataString));
                nonLeafNode.setParentNodeAddress(StringUtil.getParentAddress(dataString));
                nonLeafNode.setNodeAddress(StringUtil.getNodeAddress(dataString));
                return nonLeafNode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return node;
    }

    /**
     * 叶子节点的存储方法,传入一个叶子节点,将叶子节点中的信息toString,保存在索引文件中具体方法步骤:
     * 1.将指针指向文件的末尾,并存储
     * 2.先在索引文件中存储每次读取数据量大小的数据字节
     * 3.将要写入的字符串信息,存入byte数组,将指针指向刚读取到的位置,再次写入数据,覆盖刚刚写入的默认值
     *
     * @param node 保存的节点
     * @return 该节点保存的位置
     */
    public static long saveNodeInfo(Node node) {
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "rw")) {
            long fileEndAddress = raf.length();
            raf.seek(fileEndAddress);
            raf.write(empty);
//            clearByte(data);
            byte[] writeDataBytes = node.toString().getBytes();
//            data = node.toString().getBytes();
            raf.seek(fileEndAddress);
            raf.write(writeDataBytes);
            return fileEndAddress;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 初始化索引文件头部信息
     * 第一次运行时保存的索引文件头部信息
     * 在第一个索引文件磁盘块中，只保存根节点地址，初始保存的根节点地址为（1024）指向默认的第一块叶子节点
     * 在第二个索引文件磁盘块中，保存一个默认的叶子节点，且根节点的位置信息为当前叶子节点的位置
     */
    public static void initializeRootNode() {
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "rw")) {
            raf.seek(0);
            raf.write(empty);
            raf.seek(0);
            raf.write(dataBytes.toString().getBytes());
            LeafNode LeafNode = new LeafNode();
            raf.seek(dataBytes);
            raf.write(data);
            byte[] writeDataByte =  LeafNode.toString().getBytes();
//            data = LeafNode.toString().getBytes();
            raf.seek(dataBytes);
            raf.write(writeDataByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新索引文件中原来保存的节点信息的方法，步骤：
     * 1. 将文件指针指向，原来的数据保存地址
     * 2. 写入空白的数据，将原来的数据覆盖（目的：若不覆盖原有数据，则有可能会存在一部分原来的数据，对新的数据造成混乱）
     * 3. 将已更新了的节点对象，写入在原来的位置上
     *
     * @param node 需要更新的节点
     * @param address 该节点在文件中保存的指针位置
     */
    public static void refreshNodeInfo(Node node, long address) {
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "rw")) {
            raf.seek(address);
            raf.write(empty);
            raf.seek(address);
//            data = node.toString().getBytes();
            byte[] writeDataByte = node.toString().getBytes();
            raf.write(writeDataByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void setRootNodeAddress(long dataBytes) {
//        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "rw")) {
//            raf.seek(0);
//            raf.write(bytes);
//            raf.seek(0);
////            String addressString = String.valueOf(dataBytes);
////            raf.write(addressString.getBytes());
//            raf.writeLong(dataBytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static void refreshRootAddress(long nodeAddress) {
        try (RandomAccessFile raf = new RandomAccessFile(indexFileAddress, "rw")) {
            raf.seek(0);
            raf.write(empty);
            raf.seek(0);
//            clearByte(data);
//            data = String.valueOf(nodeAddress).getBytes();
            byte[] writeDataByte = String.valueOf(nodeAddress).getBytes();
            raf.write(writeDataByte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
//    private void clearByte(byte[] bytes){
//        for (int i = 0; i < bytes.length; i++) {
//            bytes[i] = 0;
//        }
//    }
//
//    public static void main(String[] args) {
//        LeafNode leafNode = new LeafNode();
//        leafNode.setNumber(5);
//        leafNode.setKey(new String[]{"dcascjasd", "casijdcas", "casijcasas", "casjcas", "cajksaskdas"});
//        leafNode.setValue(new String[]{"dcascjasddcascjasd", "casijdcasdcascjasd", "casijcasasdcascjasd", "casjcasdcascjasd", "cajksaskdasdcascjasd"});
//        leafNode.setParentNodeAddress(123456789);
//        leafNode.setRightNodeAddress(9876543210L);
//        saveLeafNodeinfo(leafNode);
//    }


}




