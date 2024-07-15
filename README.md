# UCP-with-JBoss
Integration of UCP with WildFly

Above Example is to Integarte UCP with WildFly server.

Steps: 
  1. Create new  @Startup class with @Poststartup which will bind your DataSource with UCP
  2. Override getConnection of javax.sql.DataSource which will be invoke your cutomised getConnection for UCP wrapper class.
  3. Override interface TenantIdentifierInterface so return your tenant ..
  4. Based n Teanat this code will provide you DB connection for tenant specific PDB.
