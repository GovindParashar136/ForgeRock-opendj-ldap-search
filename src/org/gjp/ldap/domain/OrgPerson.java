package org.gjp.ldap.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gjp.ldap.util.DateUtil;

public class OrgPerson {

	private String uid;
	private String postalAddress;	
	private String openDate;
	private String closeDate;	
	private String dn;
	private String sn;
	private String cn;
	private String o;
	private String uniqueIdentifier;
	private String idCode;
	private String country;
	private String roles;
	private Set<OrgPersonRole> orgPersonRoles = new HashSet<>();
	public void addOrgPersonRole(OrgPersonRole orgPersonRole){
		if(orgPersonRole.getDn().contains(this.uniqueIdentifier) && DateUtil.closeDateValidation(orgPersonRole.getCloseDate())){
			this.orgPersonRoles.add(orgPersonRole);			
		}
		
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getPostalAddress() {
		return postalAddress;
	}
	public void setPostalAddress(String postalAddress) {
		this.postalAddress = postalAddress;
	}
	public String getOpenDate() {
		return openDate;
	}
	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
	public String getCloseDate() {
		return closeDate;
	}
	public void setCloseDate(String closeDate) {
		this.closeDate = closeDate;
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
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getO() {
		return o;
	}
	public void setO(String o) {
		this.o = o;
	}
	public String getUniqueIdentifier() {
		return uniqueIdentifier;
	}
	public void setUniqueIdentifier(String uniqueIdentifier) {
		this.uniqueIdentifier = uniqueIdentifier;
	}
	public String getIdCode() {
		return idCode;
	}
	public void setIdCode(String idCode) {
		this.idCode = idCode;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getRoles() {
		return roles;
	}
	public void setRoles(String roles) {
		this.roles = roles;
	}
	public Set<OrgPersonRole> getOrgPersonRoles() {
		return orgPersonRoles;
	}
	public void setOrgPersonRoles(Set<OrgPersonRole> orgPersonRoles) {
		this.orgPersonRoles = orgPersonRoles;
	}
	
	

}
