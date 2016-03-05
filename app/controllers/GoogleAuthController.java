package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import models.travelo.AuthUser;
import models.travelo.User;
import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GoogleAuthController extends Controller {

	private static final String GOOGLE_APP_ID = Play.application().configuration().getString("GOOGLE_APP_ID");
	private static final String GOOGLE_APP_SECRET = Play.application().configuration().getString("GOOGLE_APP_SECRET");
	private static final String GOOGLE_SCOPE = Play.application().configuration().getString("GOOGLE_SCOPE");
	private static final String GOOGLE_REDIRECT_URI = Play.application().configuration().getString("GOOGLE_REDIRECT_URI");
	private static final String GOOGLE_DIALOG_OAUTH = Play.application().configuration().getString("GOOGLE_DIALOG_OAUTH");
	private static final String GOOGLE_ACCESS_TOKEN = Play.application().configuration().getString("GOOGLE_ACCESS_TOKEN");

	public static Result authenticateGoogle(){
		String url = GOOGLE_DIALOG_OAUTH+"?client_id="+GOOGLE_APP_ID+"&approval_prompt=force&response_type=code&redirect_uri="+GOOGLE_REDIRECT_URI+"&scope="+GOOGLE_SCOPE;
		return redirect(url);
	}

	public static Result authCallbackGoogle(){
		try {
			String code = request().getQueryString("code");
			String url = GOOGLE_ACCESS_TOKEN;
			String urlParams = "client_id=" + GOOGLE_APP_ID + "&redirect_uri="
					+ URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8")
					+ "&client_secret=" + GOOGLE_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
			URL u = new URL(url);
			HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
			c.setRequestMethod("POST");
			c.setRequestProperty("Content-Length", Integer.toString(urlParams.getBytes().length));
			c.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(c.getOutputStream());
			wr.writeBytes(urlParams);
			wr.flush();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String token = b.toString();
			
			ObjectMapper mapper = new ObjectMapper();
			JsonNode json = mapper.readTree(token);
			token = json.path("access_token").asText();
			
			String g = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + token;
			u = new URL(g);
			URLConnection con = u.openConnection();
			in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String graph = b.toString();
			
			JsonNode userData = mapper.readTree(graph);
			AuthUser authUser = AuthUser.findBySocial("Google",userData.path("id").asText());
			if(authUser == null){
				authUser = new AuthUser();
				authUser.socialId = userData.path("id").asText();
				authUser.socialType = "Google";
				authUser.isSocial = true;
				
				User user = new User();
				user.firstName = userData.path("given_name").asText();
				user.lastName = userData.path("family_name").asText();
				user.gender = userData.path("gender").asText();
				//userData.path("picture").asText();
				user.save();
				
				authUser.linkedUserId = user.id;
				authUser.linkedUserType = User.class.getSimpleName();
			}
			authUser.sessionUuid = UUID.randomUUID().toString();
			authUser.isOnline = true;
			authUser.save();
			response().setCookie("sessionUuid", authUser.sessionUuid);
		} catch (IOException e) {
			e.printStackTrace();
			return redirect("/login");
		}
		return redirect("/");
	}


}
