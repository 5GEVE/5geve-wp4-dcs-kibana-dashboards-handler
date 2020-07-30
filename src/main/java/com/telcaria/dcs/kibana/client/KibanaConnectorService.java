package com.telcaria.dcs.kibana.client;

public interface KibanaConnectorService {

    boolean postKibanaObject(String kibanaJsonObject);

    boolean postKibanaIndexPattern(String indexPattern);

    boolean removeKibanaObject(String kibanaObjectId, String kibanaObjectType);

    boolean setOwner(String dashboardId, String user);

}
