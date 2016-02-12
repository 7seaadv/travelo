package models.base;

import static util.ut.empty;
import static util.ut.newDateJ;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.WriteResult;
import play.modules.mongodb.jackson.MongoDB;
public abstract class MyModel  {

	@javax.persistence.Id
    public Long id;
    public Long tempUuid;
    public Date createTime = newDateJ();
    public Date modifyTime = newDateJ();

    public Boolean disable = false;
    public Date disableTime = null;
    public Boolean deleted = false;
    public Date deletedTime = null;
    public Long version = 1L;
    public String uuid = "";
    public Long versionId;

    public void increaseVersion() {
        version = VersionTable.generateVersion(this.getClass());
        versionId = version;
        modifyTime = newDateJ();
    }

    public void versionSave() {
        increaseVersion();
    }

    public void save() {
        Class clazz = this.getClass();
        increaseVersion();
        JacksonDBCollection res = MongoDB.getCollection(clazz.getSimpleName(), clazz, Long.class);
        if (id == null) {
            id = IdTable.generateId(this.getClass());
            res.save(this);
        }else{
            res.updateById(this.id,this);
        }
    }

    public void update() {
        increaseVersion();
        Class clazz = this.getClass();
        JacksonDBCollection res = MongoDB.getCollection(clazz.getSimpleName(), clazz, Long.class);
        res.updateById(this.id,this);
    }
    
    @Deprecated
    public void rawDelete() {
        Class clazz = this.getClass();
        JacksonDBCollection res = MongoDB.getCollection(clazz.getSimpleName(), clazz, Long.class);
        WriteResult res2 = res.removeById(this.id);
        String error2=res2.getError();
    }

    public void markDelete() {
        increaseVersion();
        this.deleted=true;
        this.deletedTime=new Date();
        this.save();
    }

    protected  Long generateId(Class<? extends MyModel> aClass){
        IdTable res = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, String.class).findOne();
        if (res == null) {
            res = new IdTable();
            JacksonDBCollection res2 = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, Long.class);
            res2.save(res);
        }
        Long id=res.classNameToId.get(aClass.getSimpleName());
        if (empty(id)) {
            id=0L;
        }
        Long resId=id;
        id++;
        res.classNameToId.put(aClass.getSimpleName(), id);
        JacksonDBCollection res2 = MongoDB.getCollection(IdTable.class.getSimpleName(), IdTable.class, Long.class);
        res2.save(res);
        return resId;
    };

    public static List listAll(Class a){
        JacksonDBCollection collection = MongoDB.getCollection(a.getSimpleName(), a, Long.class);
        DBCursor dbCursor = collection.find();
        if (dbCursor.count() > 0) {
            return dbCursor.toArray();
        }
        return new ArrayList();
    }

    public static net.vz.mongodb.jackson.DBCursor query(Class a) {
        return MongoDB.getCollection(a.getSimpleName(), a, Long.class).find();
    }

    public static JacksonDBCollection<Object,Long> coll(Class a) {
        return MongoDB.getCollection(a.getSimpleName(), a, Long.class);
    }

    public  static MyModel findById(Class a,Long cid) {
        return (MyModel) coll(a).findOneById(cid);
    }

}
