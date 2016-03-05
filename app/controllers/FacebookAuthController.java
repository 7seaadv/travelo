package controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

import models.travelo.AuthUser;
import models.travelo.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import play.mvc.Controller;
import play.mvc.Result;

public class FacebookAuthController extends Controller {

	private static final String FB_APP_ID = "563932217111118";
	private static final String FB_APP_SECRET = "138657ad3837621579229122d86f0bf9";
	private static final String FB_SCOPE = "public_profile";
	private static final String FB_REDIRECT_URI = "http://localhost:9000/authCallbackFB";
	private static final String FB_DIALOG_OAUTH = "https://www.facebook.com/dialog/oauth";
	private static final String FB_ACCESS_TOKEN = "https://graph.facebook.com/oauth/access_token";
	
	public static Result authenticateFB(){
		String url = FB_DIALOG_OAUTH+"?client_id="+FB_APP_ID+"&redirect_uri="+FB_REDIRECT_URI+"&scope="+FB_SCOPE;
		return redirect(url);
	}

	public static Result authCallbackFB(){
		try {
			String code = request().getQueryString("code");
			String url = FB_ACCESS_TOKEN + "?"
					+ "client_id=" + FB_APP_ID + "&redirect_uri="
					+ URLEncoder.encode(FB_REDIRECT_URI, "UTF-8")
					+ "&client_secret=" + FB_APP_SECRET + "&code=" + code;
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String token = b.toString();
			if (token.startsWith("{"))
				throw new Exception("error on requesting token: " + token + " with code: " + code);
			
			String g = "https://graph.facebook.com/me?" + token;
			u = new URL(g);
			c = u.openConnection();
			in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String graph = b.toString();
			
			System.out.println("User data = "+graph);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode userData = mapper.readTree(graph);
			AuthUser authUser = AuthUser.findBySocial("Facebook",userData.path("id").asText());
			if(authUser == null){
				authUser = new AuthUser();
				authUser.socialId = userData.path("id").asText();
				authUser.socialType = "Facebook";
				authUser.isSocial = true;
				
				User user = new User();
				//user.firstName = userData.path("given_name").asText();
				//user.lastName = userData.path("family_name").asText();
				//user.gender = userData.path("gender").asText();
				//userData.path("picture").asText();
				user.save();
				
				authUser.linkedUserId = user.id;
				authUser.linkedUserType = User.class.getSimpleName();
			}
			authUser.sessionUuid = UUID.randomUUID().toString();
			authUser.isOnline = true;
			authUser.save();
			response().setCookie("sessionUuid", authUser.sessionUuid);
		} catch (Exception e) {
			e.printStackTrace();
			return redirect("/login");
		} 
		return redirect("/");
	}
	
}
