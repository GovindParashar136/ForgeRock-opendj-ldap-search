package org.gjp.ldap.scope.claim;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import org.gjp.ldap.domain.UserTree;
import org.gjp.ldap.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScopeMap {
	
	Logger logger = LoggerFactory.getLogger(ScopeMap.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, String> getComputedClaim(Map map, UserTree userTree, Map<String, String> returnMap) {
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			System.out.println("Scope Request for : -"+key);
			try {
				if(!"profile".equals(key)){
				Class c = Class.forName("org.gjp.ldap.scope.domain." + key);
				Constructor constructor = c.getDeclaredConstructor(UserTree.class);
				constructor.setAccessible(true);
				returnMap.put(key, JSONUtil.createJSON(constructor.newInstance(userTree)));
				}
				
			} catch (Exception e) {
				logger.error("ScopeMap: this scope implementation not present " + e);
			}
			if("{}".equals(returnMap.get(key))){
				returnMap.remove(key);
			}
			
		}
		return returnMap;

	}
	
}