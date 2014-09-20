cd C:\Program Files\MySQL\MySQL Server 5.6\bin
mysql -h localhost -u alfresco --password=alfresco < C:\Users\jcule\Desktop\DropSchemas.sql
cd C:\Pentaho\design-tools\data-integration
kitchen.bat /file:C:\Users\jcule\Desktop\files\wat2\bin\Reporting-Analytics\reporting-etl\src\main\resources\ETL_activity\schema_setup.kjb /level:Basic -param:db_name=alfresco -param:db_name_alfresco=alfresco -param_db_url_alfresco=jdbc:mysql://localhost:3306/alfresco
