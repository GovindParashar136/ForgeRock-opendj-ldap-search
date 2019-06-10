package org.gjp.ldap.scope.domain;

public class id_user_org {

	private String org_name;
	private String org_code;

	public id_user_org(String org_name, String org_code) {
		this.org_name = org_name;
		this.org_code = org_code;
	}

	public String getOrg_name() {
		return org_name;
	}

	public String getOrg_code() {
		return org_code;
	}
	
	

}
