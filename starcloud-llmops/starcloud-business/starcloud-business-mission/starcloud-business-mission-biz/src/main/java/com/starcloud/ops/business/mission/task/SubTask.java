package com.starcloud.ops.business.mission.task;

import lombok.Data;

import java.util.List;

@Data
public class SubTask {

    private String executeType;

    private List<Long> singleMissionIdList;

}
