# MSX Security

## Overview

This is a library that provides MSX Security based on the Spring Security OAuth2. It provides the following security supports:
1. Configuration for the OAuth2 resource server
2. CORS configuration
3. RBAC support: [SecurityContextBasedRBACUtils](src/main/java/com/cisco/msx/security/SecurityContextBasedRBACUtils.java) and [TokenBasedRBACUtils](src/main/java/com/cisco/msx/security/TokenBasedRBACUtils.java)

## Default Properties

1. [defaults-integration-security.properties](src/main/resources/defaults-integration-security.properties)
2. [defaults-rest.properties](src/main/resources/defaults-rest.properties)
3. [defaults-security.properties](src/main/resources/defaults-security.properties)

## Build
```
mvn clean install
```
