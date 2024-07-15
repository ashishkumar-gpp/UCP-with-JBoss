
package com.finastra.jboss.module;

import oracle.ucp.jdbc.JDBCConnectionPoolStatistics;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;

import static com.finastra.jboss.module.UcpPoolDataConstant.*;

public class UCPDataSourceConnection implements DataSource {
    private PoolXADataSource poolXADataSource;
    TenantIdentifierInterface tenantClass;

    public UCPDataSourceConnection() {

    }

    public UCPDataSourceConnection(PoolXADataSource poolXADataSource) {
        this();
        this.poolXADataSource = poolXADataSource;
    }
    // private static final Logger logger = LoggerFactory.getLogger(UCPDataSourceWrapper.class);

    @Override
    public Connection getConnection() throws SQLException {
        PoolXADataSource poolDS;
        Connection tenantConnection = null;

        String sTenantDataSource = "";
        String isCacheInitialingPDB = System.getProperty("ACTIVE_PDB_NAME"); // all System PDBs

// This is only for Cache intitalistion because control will hit here before Application is up and running
        if (isCacheInitialingPDB != null && !isCacheInitialingPDB.isEmpty()) {
            sTenantDataSource = isCacheInitialingPDB;
        } else {
            String className = System.getProperty("ApplicationClassImplName");
            tenantClass = TenantIdentifierInterfaceFactory.createClass(className);
            sTenantDataSource = tenantClass.tenantIdentifierResolver();
        }
        TenantDetails tenantDetail = TenantDetails.getTenantDetailsFromPropertiesFile(sTenantDataSource);

        if (sTenantDataSource == null || sTenantDataSource.isEmpty()) {
            throw new SQLException("Tenant is not Identified.");
        }
        try {
            poolDS = PoolDataSourceFactory.getPoolXADataSource(sTenantDataSource);
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 78 || ex.getErrorCode() == 73) {
                Properties config = new Properties();
                config.setProperty("connectionPoolName", UCP_SHARED_POOL_DS);
                config.setProperty("dataSourceName", sTenantDataSource);
                poolDS = PoolDataSourceFactory.getPoolXADataSource(config);
            } else {
                throw new SQLException();
            }
        }

        try {
            tenantConnection = poolDS.createXAConnectionBuilder()
                    .serviceName(tenantDetail.getServiceName())
                    .user(tenantDetail.getUser()).password(tenantDetail.getPassword())
                    .build().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //printStats(poolXADataSource);

        System.out.println("*******  Connection created successfully for *******   " + tenantConnection.getMetaData().getUserName());

        return tenantConnection;
    }

    private void printStats(PoolXADataSource poolXADataSource) {
        JDBCConnectionPoolStatistics statistic = poolXADataSource.getStatistics();

        System.out.println("Closed connections: " + statistic.getConnectionsClosedCount());
        System.out.println("Total connections: " + statistic.getTotalConnectionsCount());
        System.out.println("Borrowed connections: " + statistic.getBorrowedConnectionsCount());
        System.out.println("Available connections: " + statistic.getAvailableConnectionsCount());

//        logger.info("Closed connections: " + statistic.getConnectionsClosedCount());
//        logger.info("Total connections: " + statistic.getTotalConnectionsCount());
//        logger.info("Borrowed connections: " + statistic.getBorrowedConnectionsCount());
//        logger.info("Available connections: " + statistic.getAvailableConnectionsCount());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return poolXADataSource.getConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException("unwrap not supported");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {

    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }


    public static void main(String[] args) {
        try {
            System.setProperty("oracle.ucp.jdbc.xmlConfigFile", "file:/C:/JBoss/ucp-config.xml");

            System.setProperty("finastra.ucpPoolDataSource.tenantIdentifier", "com.finastra.jboss.module.1DefaultTenantIdentifer");
            System.setProperty("finastra.tenant.config", "C:/JBoss/tenantDetails.properties");

            System.setProperty("ApplicationClassImplName", "com.finastra.jboss.module.MyTeanatTestImpl");
            //  TenantIdentifierInterface impl = TenantIdentifierInterfaceFactory.createClass(System.getProperty("ApplicationClassImplName"));
            // impl.tenantIdentifierResolver();
            UCPDataSourceConnection dataSourceWrapper = new UCPDataSourceConnection();

            Connection testAgain = dataSourceWrapper.getConnection();
            Connection connection = dataSourceWrapper.getConnection();

            if (connection != null && connection.isValid(2)) {
                System.out.println("Connection is successful.");

                try (Statement stmt = connection.createStatement()) {
                    ResultSet rs = stmt.executeQuery("Select EVENT_NAME, BATCH_SIZE, DEFAULT_PRIORITY, FREQUENCY_IN_MINUTES, EVENT_STATUS from EVENT_DEFINITIONS");
                    if (rs.next()) {
                        System.out.println("Query executed successfully.");
                    } else {
                        System.out.println("Query did not return any results.");
                    }
                } catch (SQLException e) {
                    System.out.println("Failed to execute query.");
                    e.printStackTrace();
                }

            } else {
                System.out.println("Connection is not successful.");
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}