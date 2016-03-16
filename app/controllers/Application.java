package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import models.base.Photo;
import models.travelo.AuthUser;
import models.travelo.Location;
import models.travelo.User;
import play.libs.Json;
import play.modules.mongodb.jackson.MongoDB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import service.Secured;
import viewmodels.LocationVM;
import viewmodels.UserVM;
import views.html.login;
import views.html.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.DB;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class Application extends Controller {

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	@Security.Authenticated(Secured.class)
	public static Result landing() {
		return ok(main.render());
	}
	
	public static Result getPhotoById(Long id, String group){
		Photo p = Photo.findById(id);
		DB db = MongoDB.getCollection(User.class.getSimpleName(), User.class, Long.class).getDB();
        GridFS gridFS = new GridFS(db, "ProfilePhoto");
        GridFSDBFile imageForOutput = gridFS.find(new org.bson.types.ObjectId(p.gridFileId));
        InputStream image = imageForOutput.getInputStream();
        response().setHeader("Content-Disposition", "inline; filename*=UTF-8''" + imageForOutput.getFilename());
        response().setContentType(imageForOutput.getContentType());
        return ok(image).as(("picture/stream"));
	}
	
	public static Result uploadPhoto(){
		DB db = MongoDB.getCollection(User.class.getSimpleName(), User.class, Long.class).getDB();
        File file = request().body().asRaw().asFile();
        Photo p = new Photo();
        if (file != null) {
        	String fileName = request().getHeader("File-name");
            GridFS gridFS = new GridFS(db, "ProfilePhoto");
            GridFSInputFile gfsFile = null;
            try {
                gfsFile = gridFS.createFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gfsFile.setFilename(fileName);
            gfsFile.setContentType("image/jpg");
            gfsFile.save();
            p.gridFileId = gfsFile.getId().toString();
            p.save();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id",p.id);
        return ok(Json.toJson(map));
	}
	
	public static Result getCurrentUserProfile(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		User user = User.findById(u.linkedUserId);
		if(user == null){
			return badRequest();
		}
		UserVM vm = new UserVM();
		vm.firstName = user.firstName;
		vm.lastName = user.lastName;
		vm.gender = user.gender;
		vm.dateOfBirth = user.dateOfBirth;
		vm.aboutMe = user.aboutMe;
		vm.profilePhotoId = user.profilePhotoId;
		for(Long i:user.placesBeenToIds){
			Location l = Location.findById(i);
			LocationVM lvm = new LocationVM();
			lvm.id = l.id;
			lvm.name = l.name;
			lvm.description = l.description;
			lvm.latLng = l.latLng;
			vm.placesBeenTos.add(lvm);
		}
		return ok(Json.toJson(vm));
	}
	
	public static Result updateCurrentUserProfile(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		User user = User.findById(u.linkedUserId);
		if(user == null){
			return badRequest();
		}
		try {
			JsonNode json = request().body().asJson();
			user.firstName = json.path("firstName").asText();
			user.lastName = json.path("lastName").asText();
			user.gender = json.path("gender").asText();
			user.aboutMe = json.path("aboutMe").asText();
			user.dateOfBirth = df.parse(json.path("dateOfBirth").asText());
			ArrayNode places = (ArrayNode) json.path("placesBeenTos");
			for(int i=0;i<places.size();i++){
				JsonNode loc = places.get(i);
				if(loc.path("id").asLong() == 0){
					Location l = new Location();
					l.name = loc.path("name").asText();
					l.description = loc.path("description").asText();
					ArrayNode geos = (ArrayNode) loc.path("latLng");
					l.latLng[0] = geos.get(0).asDouble();
					l.latLng[1] = geos.get(1).asDouble();
					l.save();
					user.placesBeenToIds.add(l.id);
				}
			}
			Long photoId = json.path("profilePhotoId").asLong();
			if(photoId != 0){
				if(photoId != user.profilePhotoId){
					if(user.profilePhotoId != null){
						Photo p = Photo.findById(user.profilePhotoId);
						p.linked = false;
						p.update();
					}
					Photo p = Photo.findById(photoId);
					p.linked = true;
					p.update();
					user.profilePhotoId = photoId;
				} 
			}
			user.update();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ok();
	}

}
