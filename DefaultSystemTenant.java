package com.finastra.jboss.module;

import static com.finastra.jboss.module.UcpPoolDataConstant.SYSTEM_PDB;

public class DefaultSystemTenant implements  TenantIdentifierInterface{
    @Override
    public String tenantIdentifierResolver() {
        return TenantDetails.getTenantDetailsFromPropertiesFile(SYSTEM_PDB).getSystemPDB();
    }
}
