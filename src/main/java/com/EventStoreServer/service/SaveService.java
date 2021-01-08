package com.EventStoreServer.service;

import com.EventStoreServer.entity.RoughEventInfo;

public interface SaveService {

    boolean saveRoughEventInfo(RoughEventInfo roughEventInfo);

}
