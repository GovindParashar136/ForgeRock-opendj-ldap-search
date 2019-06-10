package org.gjp.ldap.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gjp.ldap.util.DateUtil;

public class Person {

	private String uid;
	private String givenName;
	private String dn;
	private String sn;
	private String personStatus;
	private String cn;
	private String mail;
	

	private Set<OrgPerson> orgPersons = new HashSet<>();



	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getPersonStatus() {
		return personStatus;
	}

	public void setPersonStatus(String personStatus) {
		this.personStatus = personStatus;
	}

	public Set<OrgPerson> getOrgPersons() {
		return orgPersons;
	}

	public void setOrgPersons(Set<OrgPerson> orgPersons) {
		this.orgPersons = orgPersons;
	}

	public void addOrgPerson(OrgPerson orgPerson) {
		if (orgPerson.getUid().equals(this.uid) && DateUtil.closeDateValidation(orgPerson.getCloseDate())) {

			this.orgPersons.add(orgPerson);
		}

	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	

	
	

}
