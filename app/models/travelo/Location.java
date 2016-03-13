package models.travelo;

import models.base.MyModel;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

public class Location extends MyModel {

	public String name;
	public String description;
	public Double[] latLng = new Double[2];
	
	private static JacksonDBCollection<Location, Long> coll() {
        return MongoDB.getCollection(Location.class.getSimpleName(), Location.class, Long.class);
    }

	public static Location findById(Long currentLocationId) {
		return coll().findOneById(currentLocationId);
	}
}
