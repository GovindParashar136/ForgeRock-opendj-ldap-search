package org.gjp.ldap.domain;

public class UserTree {
	
	private Person  person;

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	@Override
	public String toString() {
		return "UserTree [person=" + person + "]";
	}
	
	

}
