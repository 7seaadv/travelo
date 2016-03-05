package models.travelo;

import java.util.Date;

import models.base.MyModel;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class AuthUser extends MyModel {

	public String username;
	public String password;
	
	public Long linkedUserId;
	public String linkedUserType;
	
	public boolean isSocial = false;
	public String socialId;
	public String socialType;
	
	public String sessionUuid;
	public Date lastActive;
	public boolean isOnline = true;
	
	private static JacksonDBCollection<AuthUser, Long> coll() {
        return MongoDB.getCollection(AuthUser.class.getSimpleName(), AuthUser.class, Long.class);
    }
	
	public static AuthUser findByUsername(String username) {
		return coll().find().is("username", username).count() == 0 ? 
				null : coll().find().is("username", username).next();
	}

	public static AuthUser findBySessionUuid(String sessionUuid) {
		return coll().find().is("sessionUuid", sessionUuid).count() == 0 ? 
				null : coll().find().is("sessionUuid", sessionUuid).next();
	}

	public static AuthUser findBySocial(String socialType, String socialId) {
		return coll().find().is("isSocial", true).is("socialType", socialType).is("socialId", socialId).count() == 0 ? 
				null : coll().find().is("isSocial", true).is("socialType", socialType).is("socialId", socialId).next();
	}
}
