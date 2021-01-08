package com.EventStoreServer.service.impl;

import com.EventStoreServer.entity.LeafNode;
import com.EventStoreServer.entity.Node;
import com.EventStoreServer.entity.NonLeafNode;
import com.EventStoreServer.entity.RoughEventInfo;
import com.EventStoreServer.service.SaveService;
import com.EventStoreServer.utils.AccessUtil;

public class SaveServiceImpl implements SaveService {
    @Override
    public boolean saveRoughEventInfo(RoughEventInfo roughEventInfo) {
        long rootAddress = AccessUtil.getRootAddress();
        if (rootAddress == 0){
            AccessUtil.initializeRootNode();
        }
        Node root = AccessUtil.getRootNode();
        if (root instanceof LeafNode){
            LeafNode rootLeafNode = (LeafNode)root;
            return rootLeafNode.insert(roughEventInfo);
        }else {
            NonLeafNode rootNonLeafNode = (NonLeafNode)root;
            return rootNonLeafNode.insert(roughEventInfo);
        }
    }
}
