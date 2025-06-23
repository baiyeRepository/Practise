package com.example.mapper;

import com.example.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * Description TODO
 *
 * @ClassName com.example.mapper.TrainingRecordsMapper
 * @Date: 2025/2/12 22:50
 * @User 17900
 */
import com.example.entity.TrainingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface TrainingRecordMapper {
    /**
     * 插入培训记录
     */
    int insert(TrainingRecord trainingRecord);
    /**
     * 根据 ID 删除培训记录
     */
    int deleteById(Integer id);
    /**
     * 根据 ID 更新培训记录
     */
    int updateById(TrainingRecord trainingRecord);
    /**
     * 根据 ID 查询培训记录
     */
    TrainingRecord selectById(Integer id);
    /**
     * 分页查询培训记录
     */
    List<TrainingRecord> selectPage(@Param("trainingTopic")  String trainingTopic);


}

