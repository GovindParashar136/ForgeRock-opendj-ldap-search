package org.gjp.ldap.search;

import java.util.Iterator;

import org.forgerock.openam.ldap.LDAPUtils;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.EntryNotFoundException;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.messages.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.forgerock.util.Options;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LdapSearch {
	Logger logger = LoggerFactory.getLogger(LdapSearch.class);

	public void search(String userid) throws Exception {
		Connection connection = null;
		String jsonString=null;

		try {
			Options options = Options.defaultOptions();
			ConnectionFactory conn =LDAPUtils.newFailoverConnectionFactory(
					 LDAPUtils.getLdapUrls("localhost", 389, false),
					 "cn=Directory Manager", "Password1".toCharArray(), 200000, "SECONDS", true, true, options);
			connection = conn.getConnection();
			final ConnectionEntryReader reader = connection.search("userid=" + userid + ",ou=Emp,o=gjp",
					SearchScope.WHOLE_SUBTREE, "(objectclass=*)", "*");
			while (reader.hasNext()) {
				if (reader.isEntry()) {
					SearchResultEntry entry = reader.readEntry();
					JSONObject jsonObj = new JSONObject();
					jsonObj.put("dn", entry.getName());
					jsonString =constructAttributeJson(entry, jsonObj);
					System.out.println("Search Result:- "+jsonString);

				}
			}

		} catch (EntryNotFoundException e) {
			logger.error(e.getMessage());
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("LdapUserSearch: get attribute failed: " + e);

		} finally {

			if (connection != null) {
				connection.close();
			}
		}
	}
	private String constructAttributeJson(SearchResultEntry entry, JSONObject jsonObj) throws JSONException {
		for (org.forgerock.opendj.ldap.Attribute attribute : entry.getAllAttributes()) {
			String attributeName = attribute.getAttributeDescriptionAsString();
			String attributeValue = "";
			boolean firstValue = true;
			boolean isMultiValued = false;
			for (Iterator<?> i = attribute.iterator(); i.hasNext(); firstValue = false) {
				String value = i.next().toString();
				
				if (!firstValue) {
					attributeValue += ",";
					isMultiValued = true;
				}
				attributeValue += "\"" + value + "\"";
			}

			if (isMultiValued) {
				String startBracket = "[";
				String endBracket = "]";
				attributeValue = startBracket + attributeValue + endBracket;
			}			
			jsonObj.put(attributeName, attributeValue);
		}
		return jsonObj.toString();
	}
	
	public static void main(String[] args) {
		LdapSearch ldapSearch = new LdapSearch();
		try {
			ldapSearch.search("XXXXXXXXXXX");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
