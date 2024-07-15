package com.finastra.jboss.module;


import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

@Singleton
@Startup
public class FNDTJndiWrapper implements UcpPoolDataConstant {
    Context active ;

    FNDTJndiWrapper() throws NamingException {
        active = new InitialContext();
    }
    //private static final Logger logger = LoggerFactory.getLogger(FNDTJndiWrapper.class);
    @PostConstruct
    public void init() {

        try {
            PoolXADataSource multiTenantDS = PoolDataSourceFactory.getPoolXADataSource(UCP_SHARED_POOL_DS);

            UCPDataSourceConnection dataSourceWrapper = new UCPDataSourceConnection(multiTenantDS);
           // Context active = new InitialContext();
            // Bind 'java:/FundtechDataSource' to the UCP Pool datasource wrapper with default CDB pool property

            active.rebind(JAVA_FUNDTECH_DATA_SOURCE, dataSourceWrapper);

            //create new binding for another datasource if new datasource will be  added ...

            // Bind and 'java:/PdoDataSource' to the UCP Pool datasource wrapper with default CDB pool property
         //   Context pdoActive = new InitialContext();
           active.rebind(JAVA_ACTIVE_PDO_DATA_SOURCE, dataSourceWrapper);

        } catch (NamingException e) {
           // logger.error("NamingException occurred: " + e.getMessage(), e);
            e.printStackTrace();
        } catch (Exception e) {
           // logger.error("Exception occurred: " + e.getMessage(), e);
            e.printStackTrace();
        }

        //logger.info("  ********* FNDTJndiWrapper class loaded End  ********* ");
    }

}
