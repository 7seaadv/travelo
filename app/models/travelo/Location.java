package models.travelo;

import java.util.HashSet;
import java.util.Set;

import models.base.MyModel;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import play.modules.mongodb.jackson.MongoDB;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mongodb.BasicDBObject;

public class Location extends MyModel {

	public String name;
	public String description;
	public Double[] lngLat = new Double[2];
	
	private static JacksonDBCollection<Location, Long> coll() {
        return MongoDB.getCollection(Location.class.getSimpleName(), Location.class, Long.class);
    }

	public static Location findById(Long currentLocationId) {
		return coll().findOneById(currentLocationId);
	}

	private static Set<Long> getNearByLocationIdsByLngLat(Double[] lngLat) {
		coll().ensureIndex(new BasicDBObject("lngLat", "2dsphere"), "geospatialIdx");
		BasicDBObject myLoc = new BasicDBObject();
		myLoc.append("type", "Point");
		myLoc.append("coordinates" , lngLat);
		BasicDBObject filter = new BasicDBObject("$near", myLoc);
		filter.put("$maxDistance", 100);
		BasicDBObject query = new BasicDBObject("lngLat", filter);
		DBCursor<Location> cursor = coll().find(query);
		Set<Long> locs = new HashSet<>();
		while (cursor.hasNext()){
			Location result = cursor.next();
			locs.add(result.id);
		}
		return locs;
	}

	public static Set<Long> getNearByLocationIds(ArrayNode places) {
		Set<Long> locs = new HashSet<>();
		for(int i=0;i<places.size();i++){
			JsonNode loc = places.get(i);
			ArrayNode geos = (ArrayNode) loc.path("lngLat");
			Double[] lngLat = new Double[2];
			lngLat[0] = geos.get(0).asDouble();//longitude
			lngLat[1] = geos.get(1).asDouble();//latitude
			locs.addAll(getNearByLocationIdsByLngLat(lngLat));
		}
		return locs;
	}
}
