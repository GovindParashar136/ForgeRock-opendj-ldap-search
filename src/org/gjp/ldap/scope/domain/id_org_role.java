package org.gjp.ldap.scope.domain;

import java.util.ArrayList;
import java.util.List;

public class id_org_role {
	
	private String person_orgid;
	private String org_name;
	private String org_code;
	private List<String> id_org_roles = new ArrayList<>();
	public String getPerson_orgid() {
		return person_orgid;
	}
	public void setPerson_orgid(String person_orgid) {
		this.person_orgid = person_orgid;
	}
	public String getOrg_name() {
		return org_name;
	}
	public void setOrg_name(String org_name) {
		this.org_name = org_name;
	}
	public String getOrg_code() {
		return org_code;
	}
	public void setOrg_code(String org_code) {
		this.org_code = org_code;
	}
	public List<String> getId_org_roles() {
		return id_org_roles;
	}
	public void setId_org_roles(List<String> id_org_roles) {
		this.id_org_roles = id_org_roles;
	}
	

}
