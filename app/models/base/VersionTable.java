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
public class VersionTable {

	@Id
    @javax.persistence.Id
    @ObjectId
    public String id;
    public Date date = new Date();
    @Embedded
    public HashMap<String, Long> classNameToVersion = new HashMap<String, Long>();

    public static synchronized Long generateVersion(Class aClass) {
        VersionTable res = null;
        List<VersionTable> res1 = MongoDB.getCollection(VersionTable.class.getSimpleName(), VersionTable.class, String.class).find().toArray();
        if (res1.size() > 0) {
            res = res1.get(0);
        }
        if (res == null) {
            res = new VersionTable();
            JacksonDBCollection<VersionTable, String> res2 = MongoDB.getCollection(VersionTable.class.getSimpleName(), VersionTable.class, String.class);
            WriteResult<VersionTable, String> result = res2.save(res);
            res.id = result.getSavedId();
        }
        Long id = res.classNameToVersion.get(aClass.getSimpleName());
        boolean noPreviousId = empty(id);
        if (noPreviousId) {
            id = 1L;
        }
        Long resId = id;
        id++;
        res.classNameToVersion.put(aClass.getSimpleName(), id);
        JacksonDBCollection<VersionTable, String> res2 = MongoDB.getCollection(VersionTable.class.getSimpleName(), VersionTable.class, String.class);
        String field = "classNameToVersion." + aClass.getSimpleName();
        DBQuery.Query secondFilter = null;
        if (noPreviousId)
            secondFilter = DBQuery.is("_id", new org.bson.types.ObjectId(res.id));
        else
            secondFilter = DBQuery.and(DBQuery.is("_id", new org.bson.types.ObjectId(res.id)),
                    DBQuery.is(field, resId));
        WriteResult<VersionTable, String> updateResult = res2.update(secondFilter, DBUpdate.set(field, id));
        int updatedCount = 0;
        try {
            updatedCount = updateResult.getN();
        } catch (Exception e) {
            Logger.info("Waiting new id for " + aClass.getSimpleName());
            return generateVersion(aClass);
        }
        if (updatedCount>0) {
        	return resId;
        } else {
            Logger.info("Waiting new id for " + aClass.getSimpleName());
            return generateVersion(aClass);
        }
    }

}
