package org.gjp.ldap.connection;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.forgerock.openam.ldap.LDAPURL;
import org.forgerock.openam.ldap.LDAPUtils;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.util.Options;
import org.gjp.ldap.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapConnectionPool {
	
	private  String host;
	private  String port;
	private  String userName;
	private  String password;
	private  int connectionMaxPoolSize;
	private  int heartBeatInterval;
	private  String heartBeatTimeUnit;
	private  boolean useStartTLS;
	private  boolean sslTrustAll;
	public LdapConnectionPool(){
		Properties prop =PropertiesUtil.getInstance();
		host =prop.getProperty("nhsid.ldap.host");
		port =prop.getProperty("nhsid.ldap.port");
		userName =prop.getProperty("nhsid.ldap.userName");
		password =prop.getProperty("nhsid.ldap.password");
		connectionMaxPoolSize =Integer.valueOf(prop.getProperty("nhsid.ldap.connectionMaxPoolSize"));
		heartBeatInterval =Integer.valueOf(prop.getProperty("nhsid.ldap.heartBeatInterval"));
		heartBeatTimeUnit =prop.getProperty("nhsid.ldap.heartBeatTimeUnit");
		useStartTLS =Boolean.valueOf(prop.getProperty("nhsid.ldap.useStartTLS"));
		sslTrustAll =Boolean.valueOf(prop.getProperty("nhsid.ldap.sslTrustAll"));
		
		
	}

	Logger logger = LoggerFactory.getLogger(LdapConnectionPool.class);

	public Connection getConnection() {
		Connection connection = null;
		try {
			Options options = Options.defaultOptions();
			Set<String> primaryServer = new HashSet<String>();
			primaryServer.add(host);
			primaryServer.add(String.valueOf(port));
			System.out.println("host     "+host);
			System.out.println("port       "+port);
			System.out.println("userName       "+userName);
			System.out.println("password       "+password);
			System.out.println("connectionMaxPoolSize     "+connectionMaxPoolSize);
			System.out.println("heartBeatInterval     "+heartBeatInterval);
			System.out.println("heartBeatTimeUnit     "+heartBeatTimeUnit);
			System.out.println("useStartTLS       "+useStartTLS);
			System.out.println("sslTrustAll        "+sslTrustAll);
			Set<LDAPURL> primaryUrls = LDAPUtils.convertToLDAPURLs(primaryServer);
			/*ConnectionFactory conn = LDAPUtils.newFailoverConnectionPool(primaryUrls, userName, password.toCharArray(),
					connectionMaxPoolSize, heartBeatInterval, heartBeatTimeUnit, useStartTLS,sslTrustAll, options);*/
			ConnectionFactory conn =LDAPUtils.newFailoverConnectionFactory(
					 LDAPUtils.getLdapUrls(host, Integer.valueOf(port), false),
					 userName, password.toCharArray(), heartBeatInterval, heartBeatTimeUnit, useStartTLS, sslTrustAll, options);
			connection = conn.getConnection();
			 
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug("LdapUserSearch : failed to create LDAP connection", e);
			
		}

		return connection;
	}

}