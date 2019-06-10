package org.gjp.ldap.scope.domain;

import org.gjp.ldap.domain.UserTree;

public class person {

	private String user_id;
	private String given_name;
	private String family_name;
	private String name;
	private String primary_org;
	private String title;
	private String idassurancelevel;

	public person(UserTree userTree) {
		this.user_id = userTree.getPerson().getUid();
		this.given_name = userTree.getPerson().getGivenName();
		this.family_name = userTree.getPerson().getSn();
		this.name = userTree.getPerson().getCn();
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getGiven_name() {
		return given_name;
	}

	public void setGiven_name(String given_name) {
		this.given_name = given_name;
	}

	public String getFamily_name() {
		return family_name;
	}

	public void setFamily_name(String family_name) {
		this.family_name = family_name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrimary_org() {
		return primary_org;
	}

	public void setPrimary_org(String primary_org) {
		this.primary_org = primary_org;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdassurancelevel() {
		return idassurancelevel;
	}

	public void setIdassurancelevel(String idassurancelevel) {
		this.idassurancelevel = idassurancelevel;
	}

}
