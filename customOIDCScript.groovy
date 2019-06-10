/*
 * Copyright 2014-2017 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
import com.iplanet.sso.SSOException
import com.sun.identity.idm.IdRepoException
import org.forgerock.oauth2.core.exceptions.InvalidRequestException
import org.forgerock.oauth2.core.UserInfoClaims
import org.forgerock.openidconnect.Claim
import com.sun.identity.idm.IdRepoException
import com.sun.identity.idm.IdType

import org.gjp.ldap.scope.claim.RequestedClaim
import org.gjp.ldap.search.LdapUserSearch
import groovy.json.JsonSlurper;
import groovy.json.JsonBuilder

/*
* Defined variables:
* logger - always presents, the "OAuth2Provider" debug logger instance
* claims - always present, default server provided claims - Map<String, Object>
* claimObjects - always present, default server provided claims - List<Claim>
* session - present if the request contains the session cookie, the user's session object
* identity - always present, the identity of the resource owner
* scopes - always present, the requested scopes
* requestedClaims - Map<String, Set<String>>
*                  always present, not empty if the request contains a claims parameter and server has enabled
*                  claims_parameter_supported, map of requested claims to possible values, otherwise empty,
*                  requested claims with no requested values will have a key but no value in the map. A key with
*                  a single value in its Set indicates this is the only value that should be returned.
* requestedTypedClaims - List<Claim>
*                       always present, not empty if the request contains a claims parameter and server has enabled
*                       claims_paramater_supported, list of requested claims with claim name, requested possible values
*                       and if claim is essential, otherwise empty,
*                       requested claims with no requested values will have a claim with no values. A claims with
*                       a single value indicates this is the only value that should be returned.
* claimsLocales - the values from the 'claims_locales' parameter - List<String>
* Required to return a Map of claims to be added to the id_token claims
*
* Expected return value structure:
* UserInfoClaims {
*    Map<String, Object> values; // The values of the claims for the user information
*    Map<String, List<String>> compositeScopes; // Mapping of scope name to a list of claim names.
* }
*/

// user session not guaranteed to be present
boolean sessionPresent = session != null
customScopeMap = [:] // Initialize empty map


/*
 * Pulls first value from users profile attribute
 *
 * @param claim The claim object.
 * @param attr The profile attribute name.
 */
 
def fromSet = { claim, attr ->
    if (attr != null && attr.size() == 1){
		attr.iterator().next()
    } else if (attr != null && attr.size() > 1){
		attr
    } else if (logger.warningEnabled()) {
        logger.warning("OpenAMScopeValidator.getUserInfo(): Got an empty result for claim=$claim");
    }
}

def getuserUid ={attr ->
if (attr != null && attr.size() == 1){
	attr.iterator().next()
}else{
	null
}
}


//String uuid="910000000001";
String uuid=getuserUid(identity.getAttribute("uid"));

// ---vvvvvvvvvv--- EXAMPLE CLAIM ATTRIBUTE RESOLVER FUNCTIONS ---vvvvvvvvvv---
/*
 * Claim resolver which resolves the value of the claim from its requested values.
 *
 * This resolver will return a value if the claim has one requested values, otherwise an exception is thrown.
 */
defaultClaimResolver = { claim ->
    if (claim.getValues().size() == 1) {
        [
          (claim.getName()): claim.getValues().iterator().next()
        ]
    } else {
        [:]
    }
}

/*
 * Claim resolver which resolves the value of the claim by looking up the user's profile.
 *
 * This resolver will return a value for the claim if:
 * # the user's profile attribute is not null
 * # AND the claim contains no requested values
 * # OR the claim contains requested values and the value from the user's profile is in the list of values
 *
 * If no match is found an exception is thrown.
 */
 
userProfileClaimResolver = { attribute, claim, identity ->

    userProfileValue = fromSet(claim.getName(), identity.getAttribute(attribute))
	
    if (userProfileValue != null && (claim.getValues() == null || claim.getValues().isEmpty() || claim.getValues().contains(userProfileValue))) {
		return [(claim.getName()): userProfileValue]
    }
    [:]
}



/*
 * Claim resolver which resolves the value of the claim by looking up the user's profile.
 *
 * This resolver will return a value for the claim if:
 * # the user's profile attribute is not null
 * # AND the claim contains no requested values
 * # OR the claim contains requested values and the value from the user's profile is in the list of values
 *
 * If the claim is essential and no value is found an InvalidRequestException will be thrown and returned to the user.
 * If no match is found an exception is thrown.
 */
essentialClaimResolver = { attribute, claim, identity ->
    userProfileValue = fromSet(claim.getName(), identity.getAttribute(attribute))
    if (claim.isEssential() && (userProfileValue == null || userProfileValue.isEmpty())) {
        throw new InvalidRequestException("Could not provide value for essential claim $claim")
    }
    if (userProfileValue != null && (claim.getValues() == null || claim.getValues().isEmpty() || claim.getValues().contains(userProfileValue))) {
        return [(claim.getName()): userProfileValue]
    } else {
        return [:]
    }
}

/*
 * Claim resolver which expects the user's profile attribute value to be in the following format:
 * "language_tag|value_for_language,...".
 *
 * This resolver will take the list of requested languages from the 'claims_locales' authorize request
 * parameter and attempt to match it to a value from the users' profile attribute.
 * If no match is found an exception is thrown.
 */
claimLocalesClaimResolver = { attribute, claim, identity ->
    userProfileValue = fromSet(claim.getName(), identity.getAttribute(attribute))
    if (userProfileValue != null) {
        localeValues = parseLocaleAwareString(userProfileValue)
        locale = claimsLocales.find { locale -> localeValues.containsKey(locale) }
        if (locale != null) {
            return [(claim.getName()): localeValues.get(locale)]
        }
    }
    return [:]
}

/*
 * Claim resolver which expects the user's profile attribute value to be in the following format:
 * "language_tag|value_for_language,...".
 *
 * This resolver will take the language tag specified in the claim object and attempt to match it to a value
 * from the users' profile attribute. If no match is found an exception is thrown.
 */
languageTagClaimResolver = { attribute, claim, identity ->
    userProfileValue = fromSet(claim.getName(), identity.getAttribute(attribute))
    if (userProfileValue != null) {
        localeValues = parseLocaleAwareString(userProfileValue)
        if (claim.getLocale() != null) {
            if (localeValues.containsKey(claim.getLocale())) {
                return [(claim.getName()): localeValues.get(claim.getLocale())]
            } else {
                entry = localeValues.entrySet().iterator().next()
                return [(claim.getName() + "#" + entry.getKey()): entry.getValue()]
            }
        } else {
            entry = localeValues.entrySet().iterator().next()
            return [(claim.getName()): entry.getValue()]
        }
    }
    return [:]
}

/*
 * Given a string "en|English,jp|Japenese,fr_CA|French Canadian" will return map of locale -> value.
 */
parseLocaleAwareString = { s ->
    return result = s.split(",").collectEntries { entry ->
        split = entry.split("\\|")
        [(split[0]): value = split[1]]
    }
}


//Prepare custom map from requested Scopes
def getRequestedScope () {
 scopes.findAll { scope -> !("openid".equals(scope) )}.each { scope ->
			customScopeMap.put(scope,"");
}}

getRequestedScope()

// Function defination of LDAP CALL for retrive user tree
def  getUserTree(String uid, Map customScopeMap){
	if(uid == null){
		return [:]
	}
	RequestedClaim requestedClaim = new RequestedClaim();
	return requestedClaim.getComputedClaim(uid, customScopeMap); 
}

//Get User Tree from DataStore
userInfo = getUserTree(uuid, customScopeMap)

def getAttributeValue (String scopName, String attributeName){
	JsonSlurper jsonSlurper = new JsonSlurper();
		jsonStringOfScopeValue= userInfo.get(scopName) 
		if(jsonStringOfScopeValue!=null){
			if(attributeName == null){
				return jsonStringOfScopeValue;
			}
        Object scopeValueObject = jsonSlurper.parseText(jsonStringOfScopeValue);
		return  scopeValueObject.get(attributeName);
		}else{
			return null
		}
		//return new JsonBuilder(scopeValueObject.get(attributeName)).toPrettyString()
}

// ---^^^^^^^^^^--- EXAMPLE CLAIM ATTRIBUTE RESOLVER FUNCTIONS ---^^^^^^^^^^---

// -------------- UPDATE THIS TO CHANGE CLAIM TO ATTRIBUTE MAPPING FUNCTIONS ---------------
/*
 * List of claim resolver mappings.
 */
// [ {claim}: {attribute retriever}, ... ]
claimAttributes = [
        "email": userProfileClaimResolver.curry("mail"),
        "address": { claim, identity -> [ "formatted" : userProfileClaimResolver("postaladdress", claim, identity) ] },
        "phone_number": userProfileClaimResolver.curry("telephonenumber"),
        "given_name": userProfileClaimResolver.curry("givenname"),
        "zoneinfo": userProfileClaimResolver.curry("preferredtimezone"),
        "family_name": userProfileClaimResolver.curry("sn"),
        "locale": userProfileClaimResolver.curry("preferredlocale"),
        "name": userProfileClaimResolver.curry("cn"),
        "uid": userProfileClaimResolver.curry("uid"),
		"roles": { claim, identity ->
        	identity.getMemberships(IdType.GROUP)
            	def array = identity.getMemberships(IdType.GROUP).collect { group ->
    	            group.name 
            	}
            ["roles": array]           
        }
  		
		
	
]


// -------------- UPDATE THIS TO CHANGE SCOPE TO CLAIM MAPPINGS --------------
/*
 * Map of scopes to claim objects.
 */
// {scope}: [ {claim}, ... ]
scopeClaimsMap = [
        "email": [ "email" ],
        "address": [ "address" ],
        "phone": [ "phone_number" ],
        "profile": [ "given_name", "zoneinfo", "family_name", "locale", "name", "uid" ],
        "roles": [ "roles" ],
       	"person": [ "user_id","given_name", "family_name", "name"],
		"orgaccess" :["id_org_roles"],
		"nationalrbacaccess" :["id_useruid","name", "id_nrbac_roles" ]
]


// ---------------- UPDATE BELOW FOR ADVANCED USAGES -------------------
if (logger.messageEnabled()) {
    scopes.findAll { s -> !("openid".equals(s) || scopeClaimsMap.containsKey(s)) }.each { s ->
        logger.message("OpenAMScopeValidator.getUserInfo()::Message: scope not bound to claims: $s")
    }
}

/*
 * Computes the claims return key and value. The key may be a different value if the claim value is not in
 * the requested language.
 */
  
def computeClaim = { claim ->
    try {
		String cName = claim.getName();
		String keyName = null
		scopeClaimsMap.findAll { scope ->
			if(!claimAttributes.containsKey(cName) && scope.getValue().contains(cName)){
				if(customScopeMap.containsKey(scope.getKey())){
					keyName = scope.getKey()
				}
			}
	}
	if(!(keyName == null)){
		Object cValue =getAttributeValue(keyName,cName)
		if(cValue != null){	
			[(cName): cValue]
		}else{
			[:]
    	}		
	}else{
        claimResolver = claimAttributes.get(claim.getName(), { claimObj, identity -> defaultClaimResolver(claim)})
		claimResolver(claim, identity)
		}
    } catch (IdRepoException e) {
		
        if (logger.warningEnabled()) {
            logger.warning("OpenAMScopeValidator.getUserInfo(): Unable to retrieve attribute=$attribute", e);
        }
    } catch (SSOException e) {
		
        if (logger.warningEnabled()) {
            logger.warning("OpenAMScopeValidator.getUserInfo(): Unable to retrieve attribute=$attribute", e);
        }
    }
}


/*
 * Converts requested scopes into claim objects based on the scope mappings in scopeClaimsMap.
 */
def convertScopeToClaims = {
	 scopes.findAll { scope -> "openid" != scope && scopeClaimsMap.containsKey(scope)
	}.collectMany { scope ->
        scopeClaimsMap.get(scope).collect { claim ->
		    new Claim(claim)
		
        }
    }
}


// Creates a full list of claims to resolve from requested scopes, claims provided by AS and requested claims
def claimsToResolve = convertScopeToClaims() + claimObjects + requestedTypedClaims

// Computes the claim return key and values for all requested claims
computedClaims = claimsToResolve.collectEntries() { claim ->
   result = computeClaim(claim)
}

// Computes composite scopes
def compositeScopes = scopeClaimsMap.findAll { scope ->
    scopes.contains(scope.key)
}

return new UserInfoClaims((Map)computedClaims, (Map)compositeScopes)