package org.gjp.ldap.search;

import org.gjp.ldap.scope.claim.RequestedClaim;

public class TestClass {
	
public static void main(String[] args) throws Exception  {
	RequestedClaim claim = new RequestedClaim();
	System.out.println(claim.getUserTree("027651619549"));
	
}
}
