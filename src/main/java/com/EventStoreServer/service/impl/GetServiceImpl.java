package com.EventStoreServer.service.impl;

import com.EventStoreServer.entity.LeafNode;
import com.EventStoreServer.entity.Node;
import com.EventStoreServer.entity.NonLeafNode;
import com.EventStoreServer.service.GetService;
import com.EventStoreServer.utils.AccessUtil;

import java.util.ArrayList;

public class GetServiceImpl implements GetService {

    /**
     * 首先获取根节点信息，将根节点信息读取到内存中，用对象保存起来，利用根节点对象的中的方法
     * 找到该关键字与根节点所对应的分支节点，再将该分支读取出来，再使用当前节点的方法，找到key
     * 所对应的分支或数据
     * @param businessObjectName 业务对象名称
     * @param businessObjectUUID 业务对象ID
     * @return 查找的到 事务数据
     */
    @Override
    public String getRoughEventInfo(String businessObjectName, String businessObjectUUID) {
        String key = businessObjectName + "_" + businessObjectUUID;
        Node rootNode = AccessUtil.getRootNode();
        if (rootNode instanceof LeafNode){
            LeafNode leafNode = (LeafNode) rootNode;
            return leafNode.find(key);
        }else {
            NonLeafNode nonLeafNode = (NonLeafNode) rootNode;
            return nonLeafNode.find(key);
        }
    }

    @Override
    public ArrayList<String> getAll(String businessObjectName, String businessObjectUUID) {
        String key = businessObjectName + "_" + businessObjectUUID;
        Node rootNode = AccessUtil.getRootNode();
        if (rootNode instanceof LeafNode){
            LeafNode leafNode = (LeafNode) rootNode;
            return leafNode.findAll(key);
        }else {
            NonLeafNode nonLeafNode = (NonLeafNode) rootNode;
            return nonLeafNode.findAll(key);
        }
    }
}
