package models.travelo;

import java.util.List;

import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;
import models.base.MyModel;

public class Message extends MyModel {

	public Long conversationId;
	public String content;
	public String senderType;
	
	private static JacksonDBCollection<Message, Long> coll() {
        return MongoDB.getCollection(Message.class.getSimpleName(), Message.class, Long.class);
    }

	public static List<Message> getAllByConversationId(Long cid) {
		return coll().find().is("conversationId", cid).toArray();
	}
}
