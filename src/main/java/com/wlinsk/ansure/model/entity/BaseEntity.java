package com.wlinsk.ansure.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.wlinsk.ansure.basic.enums.DelStateEnum;
import com.wlinsk.ansure.basic.handler.UniversalEnumHandler;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wlinsk
 * @Date: 2024/5/23
 */
@Data
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = -4231096036873018799L;
    @TableId(type = IdType.AUTO)
    protected Integer id;

    protected Date createTime;

    protected Date updateTime;

    /**
     * 删除状态
     */
    @TableField(typeHandler = UniversalEnumHandler.class,jdbcType = JdbcType.NUMERIC)
    protected DelStateEnum delState;

    /**
     * 版本号(乐观锁)
     */
    protected Long version;

    public void init() {
        this.createTime = new Date();
        this.delState = DelStateEnum.NORMAL;
        this.version = 1L;
    }
}
