package com.edaijia.drivertraceservice.domain;

import com.zhouyutong.zorm.annotation.PK;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.Document;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.Field;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.FieldType;
import com.zhouyutong.zorm.dao.jdbc.annotation.Column;
import com.zhouyutong.zorm.dao.jdbc.annotation.Table;
import com.zhouyutong.zorm.entity.IdEntity;
import lombok.Data;

import java.util.Date;

/**
 * Created by tianhong on 2018/5/20.
 */
@Document(indexName = "order_position", typeName = "order_position_type")
@Table(value = "t_order_position_tcp")
@Data
public class OrderPositionEntity implements IdEntity {

    @PK
    @Column(value = "id")
    @Field(type = FieldType.Long)
    private Long id;

    @Column(value = "order_id")
    @Field(type = FieldType.Long)
    private Long orderId;

    @Column(value = "positions")
    @Field(type = FieldType.String)
    public String positions;

    @Column(value = "create_time")
    @Field(type = FieldType.Date)
    private Date createTime;

    @Column(value = "update_time")
    @Field(type = FieldType.Date)
    private Date updateTime;

    @Column(value = "accuracy")
    @Field(type = FieldType.String)
    private String accuracy;

    @Column(value = "gps_type")
    @Field(type = FieldType.String)
    private String gpsType;

    @Column(value = "lat")
    @Field(type = FieldType.String)
    private String lat;

    @Column(value = "lng")
    @Field(type = FieldType.String)
    private String lng;

    @Column(value = "locate_time")
    @Field(type = FieldType.Long)
    private Long locate_time;

    @Column(value = "provider")
    @Field(type = FieldType.String)
    private String provider;

    @Column(value = "timestamp")
    @Field(type = FieldType.Long)
    private Long timestamp;
}
