# UCP-with-JBoss
Integration of UCP with WildFly. This Example is to establish PDB connection for specific Teanant 

DB Changes : 
1. Create one CDB
2. Create 2 PDB under CDB

Above Example is to Integarte UCP with WildFly server.

Steps: 
  1. Create new  @Startup class with @Poststartup which will bind your DataSource with UCP
  2. Override getConnection of javax.sql.DataSource which will be invoke your cutomised getConnection for UCP wrapper class.
  3. Override interface TenantIdentifierInterface so return your tenant ..
  4. Based n Teanat this code will provide you DB connection for tenant specific PDB.
