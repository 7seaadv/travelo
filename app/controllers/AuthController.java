package controllers;

import java.util.UUID;

import models.travelo.AuthUser;
import models.travelo.User;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

public class AuthController extends Controller {

	public static Result login(){
		if(request().cookie("sessionUuid") == null || request().cookie("sessionUuid").value().isEmpty()){
			return ok(login.render());
		}
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		return redirect("/");
	}
	
	public static Result authenticate(){
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String username = form.get("username");
		String password = form.get("password");
		if(username == null || username.isEmpty() 
				|| password == null || password.isEmpty()){
			return badRequest(login.render());
		}
		AuthUser u = AuthUser.findByUsername(username);
		if(u == null){
			return badRequest(login.render());
		}
		if(!u.password.equals(password)){
			return badRequest(login.render());
		}
		u.sessionUuid = UUID.randomUUID().toString();
		u.isOnline = true;
		u.update();
		response().setCookie("sessionUuid", u.sessionUuid);
		//session("name", form.get("name"));
		return redirect("/");
	}
	
	public static Result signup(){
		if(request().cookie("sessionUuid") == null || request().cookie("sessionUuid").value().isEmpty()){
			return ok(signup.render());
		}
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(signup.render());
		}
		return redirect("/");
	}
	
	public static Result register(){
		DynamicForm form = DynamicForm.form().bindFromRequest();
		String firstname = form.get("firstname");
		String lastname = form.get("lastname");
		String username = form.get("username");
		String password = form.get("password");
		String repeatpassword = form.get("repeatpassword");
		if(firstname == null || firstname.isEmpty()
				|| lastname == null || lastname.isEmpty()
				|| username == null || username.isEmpty()
				|| password == null || password.isEmpty()
				|| repeatpassword == null || repeatpassword.isEmpty()){
			return badRequest(signup.render());
		}
		if(!password.equals(repeatpassword)){
			return badRequest(signup.render());
		}
		AuthUser u = AuthUser.findByUsername(username);
		if(u != null){
			return badRequest(signup.render());
		}
		u = new AuthUser();
		u.username = username;
		u.password = password;
		
		User user = new User();
		user.firstName = firstname;
		user.lastName = lastname;
		user.save();
		
		u.linkedUserId = user.id;
		u.linkedUserType = User.class.getSimpleName();
		
		u.sessionUuid = UUID.randomUUID().toString();
		u.isOnline = true;
		u.save();
		response().setCookie("sessionUuid", u.sessionUuid);
		return redirect("/");
	}
	
	public static Result logout() {
		String sessionUuid = request().cookie("sessionUuid").value();
		AuthUser u = AuthUser.findBySessionUuid(sessionUuid);
		u.sessionUuid = null;
		u.isOnline = false;
		u.update();
        session().clear();
        response().discardCookie("sessionUuid");
        return redirect("/login");
    }
	
}
