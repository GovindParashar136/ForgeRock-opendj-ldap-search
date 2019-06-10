package org.gjp.ldap.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

	
	private static PropertiesUtil propertiesUtil = null;
	public static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

	
	public static Properties getInstance() {
		if (propertiesUtil == null)
			propertiesUtil = new PropertiesUtil();

		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("/home/local/oidc/oidc-config.properties"));

		} catch (IOException e) {
			logger.error(">>>>>>>>>>>>>>>>>>>>>>>"+e.getMessage());
			
		}

		return properties;
	}

}
