package org.gjp.ldap.scope.claim;

import java.util.HashMap;
import java.util.Map;

import org.gjp.ldap.search.LdapUserSearch;
import org.gjp.ldap.util.JSONUtil;


public class RequestedClaim{
	public String getUserTree(String uuid) throws Exception  {
	LdapUserSearch ldapUserSearch = new LdapUserSearch();
	return JSONUtil.createJSON(ldapUserSearch.getUserTree(uuid));
	}
	
	public Map<String, String> getComputedClaim(String uuid,Map map) throws Exception  {
		LdapUserSearch ldapUserSearch = new LdapUserSearch();
		Map<String, String> returnMap = new HashMap<>();
		ScopeMap scopMap = new ScopeMap();
		return scopMap.getComputedClaim(map, ldapUserSearch.getUserTree(uuid), returnMap);
		 
	}
	
}