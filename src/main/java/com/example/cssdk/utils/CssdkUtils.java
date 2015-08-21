package com.example.cssdk.utils;


import com.interwoven.cssdk.client.axis.generated.CSException;
import com.interwoven.cssdk.factory.*;
import com.interwoven.cssdk.common.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Locale;
import java.util.Properties;

public class CssdkUtils {

    private static Log log = LogFactory.getLog(CssdkUtils.class);

    /**
     * Use this method for the remote client connection to the server. As this method uses webservices
     * for the connection. So jars required webservices client will be needed along with few TeamSite Jars.
     * cssdksoap.jar is needed.
     * @param tsServerUrl
     * @param user
     * @param password
     * @return SOAP Client for Content Services
     */
    public static CSClient getSoapClient(final String tsServerUrl, final String user, final String password){
        Properties properties  = new Properties();

        //Set the property for CSFactory
        properties.setProperty(
                "com.interwoven.cssdk.factory.CSFactory",
                "com.interwoven.cssdk.factory.CSSOAPFactory");

        //Set the property for service URL
        properties.setProperty(
                "serviceBaseURL",
                tsServerUrl);


        CSFactory factory = CSFactory.getFactory(properties);
        if(log.isDebugEnabled()) log.debug("Factory initialized: " + factory);

        return setUpClient(user, password, factory);
    }

    /**
     * Use this method for the client within the server
     * cssdkjava.jar is needed
     * @param fqdnOfTSServer
     * @param user
     * @param password
     * @return Local Java client for Content Services
     */
    public static CSClient getJavaClient(final String fqdnOfTSServer, final String user, final String password){
        Properties properties  = new Properties();

        //Set the property for CSFactory

        //Mandatory property
        properties.setProperty(
                "com.interwoven.cssdk.factory.CSFactory",
                "com.interwoven.cssdk.factory.CSJavaFactory");

        //Mandatory property
        properties.setProperty(
                "defaultTSServer",
                fqdnOfTSServer
        );

        //Mandatory property
        properties.setProperty(
                "ts.server.os",
                "lin" // Possible values "win", "sol" or "lin" for Windows, Solaris and Linux
        );


        CSFactory factory = CSFactory.getFactory(properties);
        if(log.isDebugEnabled()) log.debug("Factory initialized: " + factory);

        return setUpClient(user, password, factory);
    }

    /**
     *
     * @param user
     * @param password
     * @param factory
     * @return
     */
    private static CSClient setUpClient(final String user, final String password, final CSFactory factory) {
        CSClient client = null;
        try {
            CSVersion version = null;
            version = factory.getClientVersion();  printVersionDetails("Client", version);
            version = factory.getServerVersion();  printVersionDetails("Server", version);

            // Construct the CSClient object.
            client = factory.getClient(
                    user,           // UserName
                    "",                // Roles: No-Op
                    password,           // Password
                    Locale.getDefault(),   // Locale
                    "Example",              // Application Context
                    null );                // TeamSite HostName [ will be read from properties object ]

            log.info("CSClient! - User: " + client.getCurrentUser().getName());
        }
        catch( CSException e ) {
            log.error("Exception occured while retrieving CSClient object!");
            e.printStackTrace();
        }
        return client;
    }

    private static void printVersionDetails( String who, CSVersion version ){
        log.info("CSSDK " + who + " Version: " +
                version.getMajorNumber() + "." +
                version.getMinorNumber() + "." +
                version.getPatchNumber());
    }

}
