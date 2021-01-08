package com.EventStoreServer.entity;

import com.EventStoreServer.utils.AccessUtil;

public abstract class Node {
    // 暂定每个节点可以存储9个数据
    final static Integer treeOrder = 9;

    // 当前节点键的个数
    Integer number;

    // 当前节点的关键字数组
    String[] key;

    // 当前节点所保存的信息（叶子节点保存数据，非叶子节点保存子节点的位置信息）
    String[] value;

    long nodeAddress;

    // 父节点的地址
    long parentNodeAddress;

    abstract String find(String key);

    abstract boolean insert(RoughEventInfo info);

    Node() {
        this.number = 0;
        this.key = new String[treeOrder];
        this.value = new String[treeOrder];
        this.parentNodeAddress = 0;
        this.nodeAddress = 0;
    }

    public long getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(long nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String[] getKey() {
        return key;
    }

    public void setKey(String[] key) {
        this.key = key;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public long getParentNodeAddress() {
        return parentNodeAddress;
    }

    public void setParentNodeAddress(long parentNodeAddress) {
        this.parentNodeAddress = parentNodeAddress;
    }

    /**
     * 更新父节点中关键字信息的方法，当前节点最大关键字变更时，调用此方法对当前节点的父节点中的节点信息进行更新
     *
     * @param maxKey 当前节点的最大关键字
     */
    public void refreshKey(String maxKey) {
//        AccessUtil accessUtil = new AccessUtil();
        if (this.parentNodeAddress != 0) {
            Node parentNode = AccessUtil.getNode(this.parentNodeAddress);
            NonLeafNode Node = (NonLeafNode) parentNode;
            if (maxKey.compareTo(Node.key[Node.number - 1]) > 0) {
                Node.key[Node.number - 1] = maxKey;
                AccessUtil.refreshNodeInfo(Node, this.parentNodeAddress);
                // 利用递归，实现所有父节点的节点更新，直到找到根节点。
                Node.refreshKey(maxKey);
            }
//        } else {
//            // 如果当前节点是叶子节点，其parentNode == 0
//            if (this instanceof LeafNode) {
//                // parentAddress == 0 说明当前是第一次插入，将当前叶子节点的父节点地址指向第二个索引文件磁盘块中的非叶子节点
//                this.parentNodeAddress = AccessUtil.dataBytes;
//                // 将当前的叶子节点信息，放在非叶子节点中
//                Node node = accessUtil.getNode(this.parentNodeAddress);
//                LeafNode rootNode = (LeafNode) node;
//                rootNode.number = 1;
//                rootNode.key[0] = this.key[number - 1];
//                rootNode.value[0] = String.valueOf(accessUtil.getRootAddress());
//                accessUtil.setRootNodeAddress(AccessUtil.dataBytes);
//            } else {
//                // 当前节点为非叶子节点，其parentNode == 0 说明当前节点为非叶子节点类型的根节点，且不需要分裂，
//                // 所以只需要更新节点信息就可以，调用此方法之前，以及更新了当前节点的信息，所以此次不做操作，
//                // 只需将当前节点重新写回到原来的位置即可
//            }

        }

    }
}
