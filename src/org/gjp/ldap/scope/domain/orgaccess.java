package org.gjp.ldap.scope.domain;

import java.util.ArrayList;
import java.util.List;

import org.gjp.ldap.domain.OrgPerson;
import org.gjp.ldap.domain.UserTree;

public class orgaccess {

	private List<id_org_role> id_org_roles = new ArrayList<>();

	public orgaccess(UserTree tree) {

		for (OrgPerson orgPerson : tree.getPerson().getOrgPersons()) {
			id_org_role nhsid_org_role = new id_org_role();
			nhsid_org_role.setOrg_name(orgPerson.getO());
			nhsid_org_role.setOrg_code(orgPerson.getIdCode());
			nhsid_org_role.setPerson_orgid(orgPerson.getUniqueIdentifier());
			id_org_roles.add(nhsid_org_role);
		}

	}

	public List<id_org_role> getId_org_roles() {
		return id_org_roles;
	}
	
	
}
