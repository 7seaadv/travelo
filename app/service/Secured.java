package service;

import models.travelo.AuthUser;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured  extends Security.Authenticator{

	@Override
	public String getUsername(Context ctx) {
		if(ctx.request().cookie("sessionUuid") == null){
			return null;
		}
		String session = ctx.request().cookie("sessionUuid").value();
		if(session == null || session.isEmpty()){
			return null; 
		}
		try {
			AuthUser u = AuthUser.findBySessionUuid(session);
			if(u != null){
				return session;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Result onUnauthorized(Context ctx) {
		return redirect("/login");
	}
}
