docker run --name oraclexe \ 
  -p 51521:1521 \
  -p 55500:5500 \ 
  -v X:\projects\asset-management\asset-management-funds\target\oradata:/opt/oracle/oradata \
  -v X:\projects\asset-management\asset-management-funds\src\it\resources\oracle\setup:/opt/oracle/scripts/setup \
  -e ORACLE_PWD=P4$$word123 \
  -e ORACLE_CHARACTERSET=AL32UTF8 \ 
  oracle/database:18.4.0-xe