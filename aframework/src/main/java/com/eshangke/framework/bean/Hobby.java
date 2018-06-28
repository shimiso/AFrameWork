package com.eshangke.framework.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by huochangsheng on 2018/6/25.
 */
@Entity
public class Hobby {
    @Id
    private Long id;
    private String aihao;
    @Generated(hash = 1262461130)
    public Hobby(Long id, String aihao) {
        this.id = id;
        this.aihao = aihao;
    }
    @Generated(hash = 23756816)
    public Hobby() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAihao() {
        return this.aihao;
    }
    public void setAihao(String aihao) {
        this.aihao = aihao;
    }

}
