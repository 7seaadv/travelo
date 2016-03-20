package controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.base.Photo;
import models.travelo.AuthUser;
import models.travelo.Conversation;
import models.travelo.Location;
import models.travelo.Message;
import models.travelo.User;
import play.libs.Json;
import play.modules.mongodb.jackson.MongoDB;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import service.MyUtils;
import service.Secured;
import viewmodels.ConversationVM;
import viewmodels.LocationVM;
import viewmodels.MessageVM;
import viewmodels.UserBasicInfoVM;
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
	
	public static Result getUserBasicInfo(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		User user = User.findById(u.linkedUserId);
		if(user == null){
			return badRequest();
		}
		UserBasicInfoVM vm = new UserBasicInfoVM();
		vm.screenName = user.firstName + " " + user.lastName;
		vm.profilePhotoId = user.profilePhotoId;
		return ok(Json.toJson(vm));
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
			lvm.lngLat = l.lngLat;
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
			if(MyUtils.validateString(json.path("gender").asText())){
				user.gender = json.path("gender").asText();
			}
			if(MyUtils.validateString(json.path("aboutMe").asText())){
				user.aboutMe = json.path("aboutMe").asText();
			}
			if(MyUtils.validateString(json.path("dateOfBirth").asText())){
				user.dateOfBirth = df.parse(json.path("dateOfBirth").asText());
			}
			ArrayNode places = (ArrayNode) json.path("placesBeenTos");
			for(int i=0;i<places.size();i++){
				JsonNode loc = places.get(i);
				if(loc.path("id").asLong() == 0){
					Location l = new Location();
					l.name = loc.path("name").asText();
					l.description = loc.path("description").asText();
					ArrayNode geos = (ArrayNode) loc.path("lngLat");
					l.lngLat[0] = geos.get(0).asDouble();//longitude
					l.lngLat[1] = geos.get(1).asDouble();//latitude
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
	
	public static Result searchExperts(){
		AuthUser au = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		JsonNode json = request().body().asJson();
		ArrayNode places = (ArrayNode) json.path("places");
		Set<Long> locs = Location.getNearByLocationIds(places);
		List<User> users = User.getUsersByPlacesBeenTos(locs);
		List<UserVM> experts = new ArrayList<>();
		for(User u:users){
			if(au != null && au.linkedUserId == u.id){
				continue;
			}
			UserVM vm = new UserVM();
			vm.id = u.id;
			vm.firstName = u.firstName;
			vm.lastName = u.lastName;
			vm.profilePhotoId = u.profilePhotoId;
			vm.aboutMe = u.aboutMe;
			experts.add(vm);
		}
		return ok(Json.toJson(experts));
	}
	
	public static Result sendRequestMessage(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		JsonNode json = request().body().asJson();
		Long expertId = json.path("toUserId").asLong();
		Long travellerId = u.linkedUserId;
		Conversation c = Conversation.findByTravellerAndExpert(travellerId,expertId);
		if(c == null){
			c = new Conversation();
			c.travellerId = travellerId;
			c.expertId = expertId;
			c.save();
		}
		Message m = new Message();
		m.conversationId = c.id;
		m.content = json.path("content").asText();
		m.senderType = "Traveller";
		m.save();
		return ok();
	}
	
	public static Result sendMessage(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		JsonNode json = request().body().asJson();
		Long cid = json.path("conversationId").asLong();
		Conversation c = Conversation.findById(cid);
		if(c == null){
			return badRequest();
		}
		Message m = new Message();
		m.conversationId = c.id;
		m.content = json.path("content").asText();
		if(u.linkedUserId == c.travellerId){
			m.senderType = "Traveller";
		} else {
			m.senderType = "Expert";
		}
		m.save();
		return ok();
	}
	
	public static Result getUserConversations(){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		List<Conversation> convs = Conversation.getUserConversations(u.linkedUserId);
		List<ConversationVM> conversations = new ArrayList<>();
		for(Conversation c:convs){
			ConversationVM vm = new ConversationVM();
			vm.id = c.id;
			vm.travellerId = c.travellerId;
			vm.expertId = c.expertId;
			User user = null;
			if(u.linkedUserId.equals(c.travellerId)){
				vm.selfType = "Traveller";
				user = User.findById(c.expertId);
			} else {
				vm.selfType = "Expert";
				user = User.findById(c.travellerId);
			}
			vm.endUserPhotoId = user.profilePhotoId;
			vm.endUserName = user.firstName + " " + user.lastName;
			conversations.add(vm);
		}
		return ok(Json.toJson(conversations));
	}
	
	public static Result getConversationDetails(Long id){
		AuthUser u = AuthUser.findBySessionUuid(request().cookie("sessionUuid").value());
		if(u == null){
			return ok(login.render());
		}
		Conversation c = Conversation.findById(id);
		if(c == null || (u.linkedUserId != c.travellerId && u.linkedUserId != c.expertId)){
			return badRequest();
		}
		ConversationVM cvm = new ConversationVM();
		cvm.id = c.id;
		cvm.travellerId = c.travellerId;
		cvm.expertId = c.expertId;
		User user = null;
		if(u.linkedUserId.equals(c.travellerId)){
			cvm.selfType = "Traveller";
			user = User.findById(c.expertId);
		} else {
			cvm.selfType = "Expert";
			user = User.findById(c.travellerId);
		}
		cvm.endUserPhotoId = user.profilePhotoId;
		cvm.endUserName = user.firstName + " " + user.lastName;
		List<Message> messages = Message.getAllByConversationId(c.id);
		List<MessageVM> msgs = new ArrayList<>();
		for(Message m:messages){
			MessageVM vm = new MessageVM();
			vm.id = m.id;
			vm.conversationId = m.id;
			vm.senderType = m.senderType;
			vm.createTime = m.createTime;
			vm.content = m.content;
			msgs.add(vm);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("Conversation", cvm);
		map.put("Messages", msgs);
		return ok(Json.toJson(map));
	}

}
