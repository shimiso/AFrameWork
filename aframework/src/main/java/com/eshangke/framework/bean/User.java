package com.eshangke.framework.bean;



import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToOne;
import com.eshangke.framework.gen.DaoSession;
import com.eshangke.framework.gen.HobbyDao;
import com.eshangke.framework.gen.UserDao;

/**
 * Created by huochangsheng on 2018/6/20.
 */

@Entity
public class User {

    @Id
    private Long id;     //ID 此处的ID必须是long的封装类否则会出现约束错误
    private String name; //姓名
    private String sex;  //性别
    private String age;  //年龄
    private boolean check;  //复选框
    private Long hobbyId;
    @ToOne(joinProperty = "hobbyId")
    private Hobby hobby;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1507654846)
    private transient UserDao myDao;
    @Generated(hash = 2039329415)
    public User(Long id, String name, String sex, String age, boolean check,
            Long hobbyId) {
        this.id = id;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.check = check;
        this.hobbyId = hobbyId;
    }
    @Generated(hash = 586692638)
    public User() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSex() {
        return this.sex;
    }
    public void setSex(String sex) {
        this.sex = sex;
    }
    public String getAge() {
        return this.age;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public boolean getCheck() {
        return this.check;
    }
    public void setCheck(boolean check) {
        this.check = check;
    }
    public Long getHobbyId() {
        return this.hobbyId;
    }
    public void setHobbyId(Long hobbyId) {
        this.hobbyId = hobbyId;
    }
    @Generated(hash = 1822595352)
    private transient Long hobby__resolvedKey;
    /** To-one relationship, resolved on first access. */
    @Generated(hash = 1298419267)
    public Hobby getHobby() {
        Long __key = this.hobbyId;
        if (hobby__resolvedKey == null || !hobby__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            HobbyDao targetDao = daoSession.getHobbyDao();
            Hobby hobbyNew = targetDao.load(__key);
            synchronized (this) {
                hobby = hobbyNew;
                hobby__resolvedKey = __key;
            }
        }
        return hobby;
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 647400152)
    public void setHobby(Hobby hobby) {
        synchronized (this) {
            this.hobby = hobby;
            hobbyId = hobby == null ? null : hobby.getId();
            hobby__resolvedKey = hobbyId;
        }
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }
    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }
    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2059241980)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getUserDao() : null;
    }

}

