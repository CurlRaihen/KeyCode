# Online Key Management System
KeyCode is an online key management system for access management at a university. Users can request access authorizations, which in turn can be approved or denied by higher-level authorities (administrative staff and managers).  Everyone can view their keycards with the respective access authorizations. The system administrator role can view statistics about the system through its dashboard. 

## Installation instructions
1. Import the data model (https://github.com/CurlRaihen/KeyCode/blob/main/DATENBANK%20fix.sql) at your sql workbench
2. Run the Java Spring Application (https://github.com/CurlRaihen/KeyCode/blob/main/src/main/java/com/group7/project/ProjectApplication.java)

## Use instructions
1. Please ignore the warning about the expired HTTPS certificat
2. You can find a user manuel (in german). The system itself can be used in german and english
3. The Application can be found in your browser under http://localhost:8080 or https://localhost:8443
4. Logins: user (name:Thomas ; pw:Thomas123), administrative stuff (name:Henrik ; pw: Henrik123), administrative manager (name:Moritz ; pw:Moritz123), system admin (name:Maxi ; pw:Maxi123)

