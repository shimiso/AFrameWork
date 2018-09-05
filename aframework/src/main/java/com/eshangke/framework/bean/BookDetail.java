package com.eshangke.framework.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 描述:
 * 作者: Shims
 * 时间: ${date} ${time}
 */
@Entity
public class BookDetail {
    @Id(autoincrement = true)
    private Long id;
    private String name;
    private int age;
    @Generated(hash = 1433728360)
    public BookDetail(Long id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    @Generated(hash = 467010836)
    public BookDetail() {
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
    public int getAge() {
        return this.age;
    }
    public void setAge(int age) {
        this.age = age;
    }

}
