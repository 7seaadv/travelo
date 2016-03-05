package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import service.Secured;
import views.html.*;

public class Application extends Controller {

	@Security.Authenticated(Secured.class)
	public static Result landing() {
		return ok(main.render());
	}

}
