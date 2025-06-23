package com.example.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName com.example.entity.TrainingRecord
 * @Date: 2025/2/12 21:49
 * @User 17900
 */
public class TrainingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    /** ID */
    private Integer id;
    /** 救助员ID */
    private Integer volunteerId;
    /** 培训日期 */
    private Date trainingDate;
    /** 培训主题 */
    private String trainingTopic;
    /** 培训时长（小时） */
    private Integer duration;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getVolunteerId() {
        return volunteerId;
    }
    public void setVolunteerId(Integer volunteerId) {
        this.volunteerId = volunteerId;
    }
    public Date getTrainingDate() {
        return trainingDate;
    }
    public void setTrainingDate(Date trainingDate) {
        this.trainingDate = trainingDate;
    }
    public String getTrainingTopic() {
        return trainingTopic;
    }
    public void setTrainingTopic(String trainingTopic) {
        this.trainingTopic = trainingTopic;
    }
    public Integer getDuration() {
        return duration;
    }
    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
