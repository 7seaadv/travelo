package models.travelo;

import java.util.ArrayList;
import java.util.List;

import models.base.MyModel;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

import com.mongodb.BasicDBObject;

public class Conversation extends MyModel {

	public Long expertId;
	public Long travellerId;
	
	private static JacksonDBCollection<Conversation, Long> coll() {
        return MongoDB.getCollection(Conversation.class.getSimpleName(), Conversation.class, Long.class);
    }

	public static Conversation findByTravellerAndExpert(Long travellerId, Long expertId) {
		return coll().find().is("travellerId", travellerId).is("expertId", expertId).count() == 0 ? 
				null : coll().find().is("travellerId", travellerId).is("expertId", expertId).next();
	}

	public static List<Conversation> getUserConversations(Long userId) {
		List<BasicDBObject> queries= new ArrayList<BasicDBObject>();
		queries.add(new BasicDBObject("expertId", userId));
		queries.add(new BasicDBObject("travellerId", userId));
		BasicDBObject or = new BasicDBObject();
    	or.put("$or", queries);
		return coll().find(or).toArray();
	}

	public static Conversation findById(Long id) {
		return coll().findOneById(id);
	}
	
}
