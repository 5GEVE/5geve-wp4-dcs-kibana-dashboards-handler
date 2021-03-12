package com.telcaria.dcs.kibana.client;

public interface KibanaConnectorService {

    boolean postKibanaObject(String kibanaJsonObject);

    boolean putKibanaIndexPattern(String indexPattern);

    boolean removeKibanaObject(String kibanaObjectId, String kibanaObjectType);

    boolean setOwner(String dashboardId, String user);

    String getIndexPatternFields(String indexPattern);

}
