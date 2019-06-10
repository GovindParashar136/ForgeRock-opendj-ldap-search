package org.gjp.ldap.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.EntryNotFoundException;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.messages.SearchResultEntry;
import org.forgerock.opendj.ldif.ConnectionEntryReader;
import org.gjp.ldap.connection.LdapConnectionPool;
import org.gjp.ldap.domain.OrgPerson;
import org.gjp.ldap.domain.OrgPersonRole;
import org.gjp.ldap.domain.Person;
import org.gjp.ldap.domain.UserTree;
import org.gjp.ldap.util.JSONUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapUserSearch {
	Logger logger = LoggerFactory.getLogger(LdapUserSearch.class);
	private String objectClass = "objectClass";

	public UserTree getUserTree(String uid) throws Exception {
		Connection connection = null;
		// JSON buckets to store results, based on object classes
		String[] objectClasses = new String[3];
		objectClasses[0] = "person";
		objectClasses[1] = "orgPerson";
		objectClasses[2] = "orgPersonRole";
		List<Object> list = new ArrayList<>();
		UserTree userTree = new UserTree();

		try {
			LdapConnectionPool connectionPool = new LdapConnectionPool();
			connection = connectionPool.getConnection();

			final ConnectionEntryReader reader = connection.search("uid=" + uid + ",ou=People,o=gjp",
					SearchScope.WHOLE_SUBTREE, "(objectclass=*)", "*");

			/*
			 * final ConnectionEntryReader reader =
			 * connection.search("ou=People,o=nhs", SearchScope.WHOLE_SUBTREE,
			 * "(uid=" + uid + "*)", "*", "isMemberOf");
			 */

			while (reader.hasNext()) {
				if (reader.isEntry()) {
					SearchResultEntry entry = reader.readEntry();
					JSONObject jsonObj = new JSONObject();
					// Add "dn" entry by default
					jsonObj.put("dn", entry.getName());
					constructAttributeJson(entry, jsonObj);
					preparedDomainObject(objectClasses, jsonObj, list);
					userTree = preparedHierarchyUserTree(list);

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
		return userTree;
	}

	private UserTree preparedHierarchyUserTree(List<Object> list) {
		UserTree userTree = new UserTree();

		for (Object object : list) {
			if (object instanceof Person) {
				userTree.setPerson((Person) object);
				break;
			}
		}

		for (Object object : list) {
			if (object instanceof OrgPerson) {
				userTree.getPerson().addOrgPerson((OrgPerson) object);

			}
		}

		for (Object object : list) {
			if (object instanceof OrgPersonRole) {

				OrgPersonRole orgPersonRole = (OrgPersonRole) object;

				for (OrgPerson orgPerson : userTree.getPerson().getOrgPersons()) {

					orgPerson.addOrgPersonRole(orgPersonRole);

				}

			}
		}

		return userTree;

	}

	private void preparedDomainObject(String[] objectClasses, JSONObject jsonObj, List<Object> list)
			throws JSONException, IOException {

		for (String objClass : objectClasses) {
			if (jsonObj.has(objectClass) && jsonObj.get(objectClass).toString().toLowerCase().contains("\"" + objClass.toLowerCase() + "\"")) {
				jsonObj.remove(objectClass);
				String jsonString = jsonObj.toString().replaceAll("\\\\", "");
				jsonString = jsonString.replace("\"\"", "\"");
				jsonString = jsonString.replace("\"[", "[").replace("]\"", "]");
				if ("person".equals(objClass)) {
					list.add(JSONUtil.fromJSON(jsonString, Person.class));
				} else if ("orgPerson".equals(objClass)) {
					list.add(JSONUtil.fromJSON(jsonString, OrgPerson.class));
				} else if ("orgPersonRole".equals(objClass)) {
					jsonString = jsonString.replace("\"\"", "\"");
					list.add(JSONUtil.fromJSON(jsonString, OrgPersonRole.class));
				}
			}
		}
	}

	private void constructAttributeJson(SearchResultEntry entry, JSONObject jsonObj) throws JSONException {
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
	}
}
