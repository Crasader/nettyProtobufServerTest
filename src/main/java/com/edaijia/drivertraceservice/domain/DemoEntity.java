package com.edaijia.drivertraceservice.domain;

import com.zhouyutong.zorm.annotation.PK;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.Document;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.Field;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.FieldIndex;
import com.zhouyutong.zorm.dao.elasticsearch.annotation.FieldType;
import com.zhouyutong.zorm.dao.jdbc.annotation.Column;
import com.zhouyutong.zorm.dao.jdbc.annotation.Table;
import com.zhouyutong.zorm.entity.IdEntity;
import lombok.Data;

/**
 * @author zhoutao
 * @Description: 这是一个entity的演示
 * @date 2018/3/5
 */
@Document(indexName = "demo", typeName = "demo_type")
@Table(value = "t_demo")
@Data
public class DemoEntity implements IdEntity {
    @PK
    @Column(value = "id")
    @Field(type = FieldType.Long)
    private Long id;

    @Column(value = "user_name")
    @Field(type = FieldType.String)
    private String userName;

    @Column(value = "title")
    @Field(type = FieldType.String, index = FieldIndex.analyzed)
    private String title;

    @Column(value = "create_time")
    @Field(type = FieldType.Long)
    private Long createTime;
}
