package com.EventStoreServer.entity;

import com.EventStoreServer.utils.AccessUtil;
import com.EventStoreServer.utils.StringUtil;

import java.util.ArrayList;

public class NonLeafNode extends Node {

    public NonLeafNode() {
        super();
    }

    /**
     * 对比传入的参数key与当前节点的key数组中数据，找到对应的节点
     *
     * @param key 查找数据的关键字
     * @return 找到数据的字符串
     */
    @Override
    public String find(String key) {
        int i = 0;
        while (i < this.number) {
            // 对当前的关键字与节点键中的关键字进行比较，如果当前的关键字小于等于该节点第i个关键字，跳出循环
            if (key.compareTo(this.key[i]) <= 0) {
                break;
            }
            i++;
        }
        if (i == this.number) {
            return null;
        }
        Node sonNode = AccessUtil.getNode(Long.parseLong(this.value[i]));
        if (sonNode instanceof LeafNode) {
            LeafNode leafNode = (LeafNode) sonNode;
            return leafNode.find(key);
        } else {
            NonLeafNode nonLeafNode = (NonLeafNode) sonNode;
            return nonLeafNode.find(key);
        }
    }

    public ArrayList<String> findAll(String key){
        int i = 0;
        while (i < this.number) {
            // 对当前的关键字与节点键中的关键字进行比较，如果当前的关键字小于等于该节点第i个关键字，跳出循环
            if (key.compareTo(this.key[i]) <= 0) {
                break;
            }
            i++;
        }
        if (i == this.number) {
            return null;
        }
        Node sonNode = AccessUtil.getNode(Long.parseLong(this.value[i]));
        if (sonNode instanceof LeafNode) {
            LeafNode leafNode = (LeafNode) sonNode;
            return leafNode.findAll(key);
        } else {
            NonLeafNode nonLeafNode = (NonLeafNode) sonNode;
            return nonLeafNode.findAll(key);
        }
    }

    @Override
    public boolean insert(RoughEventInfo info) {
        String key = info.getKey();
        // 定义一个i 来记录当前插入的位置,从节点已有数据的最后一位开始比较，如果i = 0， 说明当前节点没有数据，则直接插入（调用叶子节点的插入
        // 方法，将当前信息插入）
        int i = 0;
        while (i < this.number) {
            // 对当前的关键字与节点键中的关键字进行比较，如果当前的关键字小于等于该节点第i个关键字，则将数据直接插入到该节点，否则则i++
            if (key.compareTo(this.key[i]) <= 0) {
                break;
            }
            i++;
        }
        // 子节点地址
        long sonNodeAddress;
        if (i == this.number) {
            // 从value数组中将地址信息提取出来，转化为long
            sonNodeAddress = Long.parseLong(value[i - 1]);
        } else {
            sonNodeAddress = Long.parseLong(value[i]);
        }
        // 获取子节点
        Node sonNode = AccessUtil.getNode(sonNodeAddress);
        if (sonNode instanceof LeafNode) {
            LeafNode sonLeafNode = (LeafNode) sonNode;
            return sonLeafNode.insert(info);
        } else {
            NonLeafNode sonNonLeafNode = (NonLeafNode) sonNode;
            return sonNonLeafNode.insert(info);
        }
    }

    public void refreshParentNode(Node leftNode, Node rightNode, String maxKey) {
        // 如果maxKey为null，则说明这个非叶子节点是空的，则可以直接插入两个子节点的信息
        if (maxKey == null) {
            this.nodeAddress = leftNode.parentNodeAddress;
            this.key[0] = leftNode.key[leftNode.number - 1];
            this.key[1] = rightNode.key[rightNode.number - 1];
            this.value[0] = String.valueOf(leftNode.nodeAddress);
            this.value[1] = String.valueOf(rightNode.nodeAddress);
            this.number += 2;
            AccessUtil.refreshNodeInfo(this, this.nodeAddress);
            // 将文件首部的根节点地址更新为当前非叶子节点
            AccessUtil.refreshRootAddress(this.nodeAddress);
        } else {
            //父节点不为空,则应该先寻找子节点在父节点中的位置,然后将新的节点插入到父节点中
            int i = this.number - 1;
            while (maxKey.compareTo(this.key[i]) < 0) {
                i--;
            }
            // 左子节点可直接插入
            this.key[i] = leftNode.key[leftNode.number - 1];
            this.value[i] = String.valueOf(leftNode.nodeAddress);
            // 定义两个空的数组，来临时存放数据
            String[] tempKey = new String[treeOrder + 1];
            String[] tempValue = new String[treeOrder + 1];
            System.arraycopy(this.key, 0, tempKey, 0, i + 1);
            System.arraycopy(this.value, 0, tempValue, 0, i + 1);
            // 如果MaxKey，小于当前节点的最大关键字，则表示要插入的关键字不比原节点中的所有关键字大。
            if (maxKey.compareTo(this.key[this.number - 1]) < 0) {
                System.arraycopy(this.key, i + 1, tempKey, i + 2, this.number - i - 1);
                System.arraycopy(this.value, i + 1, tempValue, i + 2, this.number - i - 1);
            }
            tempKey[i + 1] = rightNode.key[rightNode.number - 1];
            tempValue[i + 1] = String.valueOf(rightNode.nodeAddress);
            this.number++;
            // 将复制完成后的当前节点最大值，保存在一个变量中
            String maxParentKey = tempKey[number - 1];
            // 判断当前节点是否需要拆分
            if (this.number <= treeOrder) {
                // 不需要拆分时，直接将数组复制回来
                System.arraycopy(tempKey, 0, this.key, 0, this.number);
                System.arraycopy(tempValue, 0, this.value, 0, this.number);
                refreshKey(maxParentKey);
                AccessUtil.refreshNodeInfo(this, this.nodeAddress);
            } else {
                // 需要分裂时
                // 新建非叶子节点，作为拆分的右半部分；
                NonLeafNode tempNode = new NonLeafNode();
                // 将当前默认的非叶子节点写入到文件中获取该节点在文件中的位置
                tempNode.nodeAddress = AccessUtil.saveNodeInfo(tempNode);
                // 将原节点分为两个部分
                Integer middle = this.number / 2;
                tempNode.number = this.number - middle;
                this.number = middle;
                // 当当前父节点需要分裂时，其分裂出去的节点信息中所包含的字节点，的父节点需变更为当前分裂出去的节点。
                for (int j = treeOrder; j >= middle; j--) {
                    Node node = AccessUtil.getNode(Long.parseLong(tempValue[j]));
                    if (node instanceof LeafNode) {
                        LeafNode leafNode = (LeafNode) node;
                        leafNode.parentNodeAddress = tempNode.nodeAddress;
                        AccessUtil.refreshNodeInfo(leafNode, leafNode.nodeAddress);
                    } else {
                        NonLeafNode nonLeafNode = (NonLeafNode) node;
                        nonLeafNode.parentNodeAddress = tempNode.nodeAddress;
                        AccessUtil.refreshNodeInfo(nonLeafNode, nonLeafNode.nodeAddress);
                    }
                }
                // 将原来的节点中的数据，拆分成两部分，分别赋值给两个数组，原来的数组做左节点，新定义的数组做右节点
                // 将拆分的原节点的右边部分数据，赋值给新定义的叶子节点
                System.arraycopy(tempKey, middle, tempNode.key, 0, tempNode.number);
                System.arraycopy(tempValue, middle, tempNode.value, 0, tempNode.number);
                // 当前节点保留拆分的左部分数据
                // 将当前节点的数据格式化
                this.key = new String[treeOrder + 1];
                this.value = new String[treeOrder + 1];
                // 将原数据的左部分复制给当前节点
                System.arraycopy(tempKey, 0, this.key, 0, middle);
                System.arraycopy(tempValue, 0, this.value, 0, middle);
                // 判断当前节点是否有父节点，如果当前节点的父节点为空，则创建一个空的非叶子节点，作为当前节点的父节点
                // 如果父节点存在，则赋值给parentNode，如果为空，则将之前创建的父节点赋值给parentNode
                if (this.parentNodeAddress == 0) {
                    NonLeafNode parentNode = new NonLeafNode();
                    this.parentNodeAddress = AccessUtil.saveNodeInfo(parentNode);
                    maxParentKey = null;
                }
                tempNode.parentNodeAddress = this.parentNodeAddress;
                // 将两个已更新了的节点数据，写回到文件中
                AccessUtil.refreshNodeInfo(this, this.nodeAddress);
                AccessUtil.refreshNodeInfo(tempNode, tempNode.nodeAddress);
                Node node = AccessUtil.getNode(this.parentNodeAddress);
                NonLeafNode parentNode = (NonLeafNode) node;
                parentNode.refreshParentNode(this, tempNode, maxParentKey);
            }

        }
    }


    /**
     * 非叶子节点的toString方法
     * 非叶子节点字符串格式:
     * data{2;key{abcdefghigklmnopqrstuvwxy_12345678901234567890123456789012)
     * (abcdefghigklmnopqrstuvwxy_12345678901234567890123456789012)};address{
     * (1234567890)(1234567890)};F1234567890;9876543210}
     *
     * @return
     */
    @Override
    public String toString() {
        return "data{" + this.number + ";" + StringUtil.getArrayString(this.key, "key") + ";" +
                StringUtil.getArrayString(this.value, "address") + ";F" + this.parentNodeAddress +
                ";" + this.nodeAddress + "}";
    }

}
