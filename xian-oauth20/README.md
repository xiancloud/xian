#ApiFest OAuth 2.0 Server and Mapping
ApiFest consists of two main parts - the ApiFest OAuth 2.0 an OAuth 2.0 server and the ApiFest Mapping Server.

##ApiFest Mapping Server
The ApiFest Mapping Server is for people who have APIs and want to expose them to the world in a safe and convenient way.
The ApiFest Mapping Server is used to translate between the outside world and your internal systems. It helps you keep a consistent API facade.

###Features
- mappings are described in xml;
- can validate and authorize requests using the ApiFest OAuth20 Server;
- out-of-the-box flexible mapping options - several versions support, different hosts to which API requests could be directed to;
- easy to extend and customize;
- customizable error messages and responses;
- "online" change of all configurations;
- unlimited horizontal scalability;


##ApiFest OAuth 2.0 Server
The ApiFest OAuth 2.0 Server implements OAuth 2.0 server side as per http://tools.ietf.org/html/rfc6749.
It enables the usage of access tokens in ApiFest Mapping Server.

###Features
- register new client app;
- generate access token using auth code;
- generate access token using username and password - grant_type=password;
- generate access token using client credentials - grant_type=client_credentials;
- generate access token using refresh token - grant_type=refresh_token;
- revoke access token;
- validate access token;
- pluggable storage (currently supports MongoDB and Redis);
- unlimited horizontal scalability;


##ApiFest OAuth 2.0 Server Quick start:
**1. apifest-oauth.properties file**

Here is a template of the apifest-oauth.properties file:
```
oauth20.host=
oauth20.port=
oauth20.database=
db_uri=
redis.sentinels=
redis.master=
hazelcast.password=
apifest-oauth20.nodes=
custom.classes.jar=
user.authenticate.class=
custom.grant_type=
custom.grant_type.class=
```

The path to the apifest.properties file should be set as a system variable:

***-Dproperties.file***

* **Setup the ApiFest OAuth 2.0 Server host and port**

The ApiFest OAuth 2.0 Server can run on different hosts and ports.
You can define the host and the port in the apifest-oauth.properties file -

***oauth20.host*** and ***oauth20.port***

* **Setup the type of the DB (MongoDB or Redis)**

You can define the type of the DB to be used (by default MongoDB is used) - valid values are "mongodb" and "redis" (without quotes) - 

***oauth20.database***

* **Setup DB host (MongoDB)**

If MongoDB is used, define the mongo URI string in the following property in the apifest-oauth.properties file:

***db_uri***

e.g.

```db_uri = mongodb://host1:port1,host2:port2,...,hostN:portN/database?replicaSet=my_replica```

Unless overridden, the following default values are set for the connection: ```connectTimeoutMS=2```

* **Setup Redis**

If Redis is used, define Redis sentinels list(as comma-separated list) in the following property in the apifest-oauth.properties file:

***redis.sentinels***

You can define the name of Redis master in the following property in the apifest-oauth.properties file:

***redis.master***

If you use Hazelcast as a storage, you can set a password using the following property (otherwise the default Hazelcast password - dev-pass will be used):

***hazelcast.password***

In order to run ApiFest OAuth20 distributed storage, you need to setup all ApiFest OAuth20 nodes (as comma-separated list of IPs).

***apifest-oauth20.nodes***

* **Setup user authentication**

As the ApiFest OAuth 2.0 Server should be able to authenticate the user, you can implement your own user authentication as implementing com.apifest.oauth20.IUserAuthentication interface
In addition, ApiFest supports a custom grant_type and can implement your own handler for it.
The location of the jar that contains the implementation of these custom classes is set by the following property: 

***custom.classes.jar***

The custom user authentication class will be loaded by the jar defined in user.authenticate.jar. The implementation class is defined by: 

***user.authenticate.class***

* **Setup custom grant_type**

If for some reason, you need to support additional custom grant_type, you can set it using the following property:

***custom.grant_type***

The custom grant_type class implementation should be contained in the custom.classes.jar and it should be set by:

***custom.grant_type.class***

**2. Start ApiFest OAuth 2.0 Server**

You can start the ApiFest OAuth 2.0 Server with the following command:

```java -Dproperties.file=[apifest_properties_file_path] -Dlog4j.configuration=file:///[log4j_xml_file_path] -jar apifest-oauth20-0.1.2-SNAPSHOT-jar-with-dependencies.jar```

When the server starts, you will see:
```ApiFest OAuth 2.0 Server started at [host]:[port]```

##ApiFest OAuth 2.0 Endpoints:
* **/oauth20/applications** - registers client applications (POST method), returns all client applications info (GET method)
* **/oauth20/applications/[client_id]** - returns client application info (GET method), updates a client application (PUT method)
* **/oauth20/auth-codes** - issues auth codes
* **/oauth20/tokens** - issues access tokens
* **/oauth20/tokens/validate** - validates access tokens
* **/oauth20/tokens/revoke** - revokes access tokens
* **/oauth20/scopes** - creates a new scope (POST method)
* **/oauth20/scopes/[scope_name]** - returns info about a scope - name, description and expires_in (GET method),
updates a scope (PUT method), deletes a scope (DELETE method)
* **/oauth20/scopes?client_id=[client_id]** - returns scopes by client_id
* **/oauth20/tokens?client_id=[client_id]&user_id=[user_id]** - returns all active tokens for a given user and client application
