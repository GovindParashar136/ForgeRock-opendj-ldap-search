package org.gjp.ldap.scope.domain;

public class org_access {
	
	private String person_orgid;
	private String org_name;
	private String org_code;
	
	public org_access(String person_orgid, String org_name, String org_code, String gnc) {
		this.person_orgid = person_orgid;
		this.org_name = org_name;
		this.org_code = org_code;
	}
	public String getPerson_orgid() {
		return person_orgid;
	}
	public String getOrg_name() {
		return org_name;
	}
	public String getOrg_code() {
		return org_code;
	}

}
