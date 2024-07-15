package com.finastra.jboss.module;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import static com.finastra.jboss.module.UcpPoolDataConstant.*;


public class TenantDetails {
    // private static final Logger logger = LoggerFactory.getLogger(TenantDetails.class);
    private String user;
    private String password;
    private String serviceName;
    public static String systemPDB;

    public TenantDetails(String user, String password, String serviceName) {
        this.user = user;
        this.password = password;
        this.serviceName = serviceName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public static TenantDetails getTenantDetailsFromPropertiesFile(String sTenantId) {

        PropertyFileLoader propertyFileLoader = PropertyFileLoader.getInstance();

        String user = propertyFileLoader.getProperty(sTenantId + "." + USER);
        String password = propertyFileLoader.getProperty(sTenantId + "." + PASSWORD);
        String serviceName = propertyFileLoader.getProperty(sTenantId + "." + SERVICE_NAME);
        systemPDB = propertyFileLoader.getProperty(SYSTEM_PDB);

        return new TenantDetails(user, password, serviceName);
    }

    public String getSystemPDB() {
        return systemPDB;
    }
}
