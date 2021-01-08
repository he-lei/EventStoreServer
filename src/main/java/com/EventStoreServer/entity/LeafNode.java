package com.EventStoreServer.entity;

import com.EventStoreServer.utils.AccessUtil;
import com.EventStoreServer.utils.StringUtil;

import java.util.ArrayList;

public class LeafNode extends Node {

    // 右同级节点的地址
    public long rightNodeAddress;

    public LeafNode() {
        super();
        this.rightNodeAddress = 0;
    }

    public long getRightNodeAddress() {
        return rightNodeAddress;
    }

    public void setRightNodeAddress(long rightNodeAddress) {
        this.rightNodeAddress = rightNodeAddress;
    }


    @Override
    public String find(String key) {
        if (this.number == 0) {
            return null;
        }
        // 查找的位置
        int i = 0;
        while (i < this.number) {
            if (key.compareTo(this.key[i]) == 0) {
                return this.value[i];
            }
            i++;
        }
        return null;

    }

    @Override
    public boolean insert(RoughEventInfo info) {
        // 从传入的信息中获取参数key,value
        String key = info.getKey();
        String value = info.getValue();
        // 定义一个i 来记录当前插入的位置,从节点已有数据的第一位开始比较，
        // 如果i = 0， 说明当前节点没有数据，则直接插入；
        // 如果i = number && i != treeOrder 则将数据插入到number + 1处；
        // 如果i = number = treeOrder，则需要分裂当前节点
        int i = 0;
        while (i < this.number) {
            if (key.compareTo(this.key[i]) < 0) {
                break;
            }
            i++;
        }
        //复制数组,完成添加
        String[] tempKeys = new String[treeOrder + 1];
        String[] tempValues = new String[treeOrder + 1];
        // 将原来的节点中关键字小于key的数据，全部复制到新的数组中
        System.arraycopy(this.key, 0, tempKeys, 0, i);
        System.arraycopy(this.value, 0, tempValues, 0, i);
        // 如果 当前插入的数据的关键字不比当前节点的全部关键字大，则需要将i位置之后的数据复制到临时数组中
        if (i < this.number) {
            System.arraycopy(this.key, i, tempKeys, i + 1, this.number - i);
            System.arraycopy(this.value, i, tempValues, i + 1, this.number - i);
        }
        tempKeys[i] = key;
        tempValues[i] = value;
        this.number++;
        // 将复制完成后当前节点的最大关键字，保存在一个变量中
        String maxKey = tempKeys[number - 1];
        // 判断当前节点的最大关键字个数是否超过了B+树的阶，即判断是否需要分裂当前节点
        // 当不需要分裂的时候，直接将临时数组复制回原来的数组中
        if (this.number <= treeOrder) {
            System.arraycopy(tempKeys, 0, this.key, 0, this.number);
            System.arraycopy(tempValues, 0, this.value, 0, this.number);
            // 更新当前节点的父节点的方法
            refreshKey(maxKey);
            // number == 1 表示当前插入的数据为第一条，将此叶子节点写入到原来的位置上
            if (this.number == 1) {
                this.nodeAddress = AccessUtil.dataBytes;
            }
            AccessUtil.refreshNodeInfo(this, this.nodeAddress);

//            }
            return true;
//            // 当需要分裂节点时
        } else {
            // 定义一个新的叶子节点
            LeafNode tempLeafNode = new LeafNode();
            // 将初始化的右叶子节点的地址赋值给当前节点中的对应变量中
            this.rightNodeAddress = AccessUtil.saveNodeInfo(tempLeafNode);
            tempLeafNode.nodeAddress = this.rightNodeAddress;
            // 将原来的节点平分为两个部分
            Integer middle = this.number / 2;
            tempLeafNode.number = this.number - middle;
            // 判断当前节点是否有父节点，如果当前节点的父节点为空，则创建一个空的非叶子节点，作为当前节点的父节点
            // 当前节点的父节点，如果父节点存在，则赋值给parentNode，如果为空，则将之前创建的父节点赋值给parentNode
            if (this.parentNodeAddress == 0) {
                NonLeafNode parentNode = new NonLeafNode();
                this.parentNodeAddress = AccessUtil.saveNodeInfo(parentNode);
                maxKey = null;
            }
            tempLeafNode.parentNodeAddress = this.parentNodeAddress;
            // 将原来的节点中的数据，拆分成两部分，分别赋值给两个数组，原来的数组做左节点，新定义的数组做右节点
            // 将拆分的原节点的右边部分数据，赋值给新定义的叶子节点
            System.arraycopy(tempKeys, middle, tempLeafNode.key, 0, tempLeafNode.number);
            System.arraycopy(tempValues, middle, tempLeafNode.value, 0, tempLeafNode.number);
            // 当前节点保留拆分的左部分数据
            this.number = middle;
            // 将当前节点的数据格式化
            this.key = new String[treeOrder + 1];
            this.value = new String[treeOrder + 1];
            // 将原数据的左部分复制给当前节点
            System.arraycopy(tempKeys, 0, this.key, 0, middle);
            System.arraycopy(tempValues, 0, this.value, 0, middle);
            AccessUtil.refreshNodeInfo(this, this.nodeAddress);
            AccessUtil.refreshNodeInfo(tempLeafNode, tempLeafNode.nodeAddress);
            Node node = AccessUtil.getNode(this.parentNodeAddress);
            NonLeafNode parentNode = (NonLeafNode) node;
            parentNode.refreshParentNode(this, tempLeafNode, maxKey);
            return true;
        }
    }


    /**
     * 叶子节点的查找全部数据的方法，找到当前key所对应的第一个数据，判断之后的数据的关键字是否
     * 与key对应，若对应则将所有符合索引关键字的数据放在一个数组中返回
     *
     * @param key 查找所需关键字
     * @return 此关键字下所对应的全部数据
     */
    public ArrayList<String> findAll(String key) {
        if (this.number == 0) {
            return null;
        }
        // 查找的位置
        int i = 0;
        ArrayList<String> dataArray = new ArrayList<>();
        while (i < this.number) {
            if (key.compareTo(this.key[i]) == 0) {
                dataArray.add(this.value[i]);
            }
            i++;
        }
        int j = 0;
        // 控制跳出循环的条件
        int k = 0;
        // 右节点所在地址的位置
        long address = this.rightNodeAddress;
        while (k == 0) {
            Node node = AccessUtil.getNode(address);
            LeafNode rightNode = (LeafNode) node;
            while (j < rightNode.number) {
                if (rightNode.key[j].compareTo(key) == 0) {
                    dataArray.add(rightNode.value[j]);
                }
                j++;
            }
            if (j == this.number && key.compareTo(this.key[j]) != 0) {
                // 跳出循环
                k++;
            } else {
                if (rightNode.rightNodeAddress != 0) {
                    address = rightNode.rightNodeAddress;
                }else {
                    k++;
                }
                j = 0;
            }
        }
        return dataArray;
    }


    /**
     * 叶子节点的toString方法
     * 叶子节点字符串格式:
     * data{2;key{(ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)
     * (ABCDEFGHIJKLMNOPQRSTUVWXY_98765432109876543210987654321098)};value{(98
     * 7654321_ABCDEFGHIJKLMNOP_9876543210_87654321)(987654321_ABCDEFGHIJKLMNO
     * P_9876543210_87654321)};F9876543210;R9876543210;123456789}
     *
     * @return 包含叶子节点信息的字符串
     */
    @Override
    public String toString() {
        return "data{" + this.number + ";" + StringUtil.getArrayString(this.key, "key") + ";" +
                StringUtil.getArrayString(this.value, "value") + ";F" + this.parentNodeAddress +
                ";R" + this.rightNodeAddress + ";" + this.nodeAddress + "}";
    }

//    public static void main(String[] args) {
//        LeafNode leafNode = new LeafNode();
//        leafNode.number = 5;
//        leafNode.key = new String[]{"dcascjasd", "casijdcas", "casijcasas", "casjcas","cajksaskdas"};
//        leafNode.value = new String[]{"dcascjasddcascjasd", "casijdcasdcascjasd", "casijcasasdcascjasd", "casjcasdcascjasd","cajksaskdasdcascjasd"};
//        leafNode.parentNodeAddress = 123456789;
//        leafNode.rightNodeAddress = 9876543210L;
//        System.out.println(leafNode.toString());
//    }

}
