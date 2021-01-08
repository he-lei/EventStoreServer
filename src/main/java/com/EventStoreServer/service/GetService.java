package com.EventStoreServer.service;

import java.util.ArrayList;

public interface GetService {

    String getRoughEventInfo(String businessObjectName, String businessObjectUUID);

    ArrayList<String> getAll(String businessObjectName, String businessObjectUUID);
}
