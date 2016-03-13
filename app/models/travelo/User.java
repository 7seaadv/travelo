package models.travelo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.base.MyModel;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class User extends MyModel{

	public String firstName;
	public String lastName;
	public String email;
	public Date dateOfBirth;
	public Long profilePhotoId;
	public String gender;
	public String aboutMe;
	
	public List<Long> placesBeenToIds = new ArrayList<>();
	
	private static JacksonDBCollection<User, Long> coll() {
        return MongoDB.getCollection(User.class.getSimpleName(), User.class, Long.class);
    }

	public static User findById(Long linkedUserId) {
		return coll().findOneById(linkedUserId);
	}
}
