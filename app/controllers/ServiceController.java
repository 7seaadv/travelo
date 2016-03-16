package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import play.Play;
import play.mvc.Controller;
import play.mvc.Result;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceController extends Controller {

	private static final String GOOGLE_API_KEY = Play.application().configuration().getString("GOOGLE_API_KEY");

	public static Result getGooglePlaces(String input){
		try {
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+input+"&key="+GOOGLE_API_KEY;
			URL u = new URL(url.replaceAll(" ", "%20"));
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String graph = b.toString();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode data = mapper.readTree(graph);
			return ok(data);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ok();
	}
	
	public static Result getGooglePlaceDetails(String placeId){
		try {
			String url = "https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeId+"&key="+GOOGLE_API_KEY;
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(c.getInputStream()));
			String inputLine;
			StringBuffer b = new StringBuffer();
			while ((inputLine = in.readLine()) != null)
				b.append(inputLine + "\n");            
			in.close();
			String graph = b.toString();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode data = mapper.readTree(graph);
			return ok(data);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ok();
	}

}
