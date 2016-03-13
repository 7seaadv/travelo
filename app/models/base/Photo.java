package models.base;

import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class Photo extends MyModel {

	public String gridFileId;
	public boolean linked = false;
	
	private static JacksonDBCollection<Photo, Long> coll() {
        return MongoDB.getCollection(Photo.class.getSimpleName(), Photo.class, Long.class);
    }

	public static Photo findById(Long id) {
		return coll().findOneById(id);
	}
	
}
