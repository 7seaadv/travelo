package models.base;

import static util.ut.empty;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Embedded;

import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import play.Logger;
import play.modules.mongodb.jackson.MongoDB;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdTable {
    @Id
    @javax.persistence.Id
    @ObjectId
    public String id;
    public Date date = new Date();
    @Embedded
    public HashMap<String, Long> classNameToId = new HashMap<String, Long>();

    public static synchronized Long generateId(Class aClass) {
        IdTable res = null;
        List<IdTable> res1 = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, String.class).find().toArray();
        if (res1.size() > 0) {
            res = res1.get(0);
        }
        if (res == null) {
            res = new IdTable();
            JacksonDBCollection<IdTable, String> res2 = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, String.class);
            WriteResult<IdTable, String> result = res2.save(res);
            res.id = result.getSavedId();
        }
        Long id = res.classNameToId.get(aClass.getSimpleName());
        boolean noPreviousId = empty(id);
        if (noPreviousId) {
            id = 1L;
        }
        Long resId = id;
        id++;
        res.classNameToId.put(aClass.getSimpleName(), id);
        JacksonDBCollection<IdTable, String> res2 = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, String.class);
        String field = "classNameToId." + aClass.getSimpleName();
        DBQuery.Query secondFilter = null;
        if (noPreviousId)
            secondFilter = DBQuery.is("_id", new org.bson.types.ObjectId(res.id));
        else
            secondFilter = DBQuery.and(DBQuery.is("_id", new org.bson.types.ObjectId(res.id)),
                    DBQuery.is(field, resId));
        WriteResult<IdTable, String> updateResult = res2.update(secondFilter, DBUpdate.set(field, id));
        int updatedCount = updateResult.getN();
        if (updatedCount>0) {
        	return resId;
        }else {
            Logger.info("Waiting new id for " + aClass.getSimpleName());
            return generateId(aClass);
        }
    }
}
