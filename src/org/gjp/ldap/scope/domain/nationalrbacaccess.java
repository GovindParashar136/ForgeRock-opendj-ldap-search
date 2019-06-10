package org.gjp.ldap.scope.domain;

import java.util.ArrayList;
import java.util.List;

import org.gjp.ldap.domain.OrgPerson;
import org.gjp.ldap.domain.OrgPersonRole;
import org.gjp.ldap.domain.UserTree;

public class nationalrbacaccess {

	private String id_useruid;
	private String name;
	private String session_role;

	private List<id_nrbac_role> id_nrbac_roles = new ArrayList<>();

	public nationalrbacaccess(UserTree tree) {

		this.id_useruid = tree.getPerson().getUid();
		this.name = tree.getPerson().getCn();
		for (OrgPerson orgPerson : tree.getPerson().getOrgPersons()) {
			for (OrgPersonRole orgPersonRole : orgPerson.getOrgPersonRoles()) {
				id_nrbac_role nhsid_nrbac_role = new id_nrbac_role();
				nhsid_nrbac_role.setPerson_orgid(orgPerson.getUniqueIdentifier());
				nhsid_nrbac_role.setPerson_roleid(orgPersonRole.getUniqueIdentifier());
				nhsid_nrbac_role.setOrg_code(orgPersonRole.getIdCode());
				nhsid_nrbac_role.setRole_name(orgPersonRole.getJobRole());
				nhsid_nrbac_role.setRole_code(orgPersonRole.getJobRoleCode());
				id_nrbac_roles.add(nhsid_nrbac_role);
			}
		}

	}

	

	public String getId_useruid() {
		return id_useruid;
	}



	public void setId_useruid(String id_useruid) {
		this.id_useruid = id_useruid;
	}



	public List<id_nrbac_role> getId_nrbac_roles() {
		return id_nrbac_roles;
	}



	public void setId_nrbac_roles(List<id_nrbac_role> id_nrbac_roles) {
		this.id_nrbac_roles = id_nrbac_roles;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSession_role() {
		return session_role;
	}

	public void setSession_role(String session_role) {
		this.session_role = session_role;
	}


	

}
