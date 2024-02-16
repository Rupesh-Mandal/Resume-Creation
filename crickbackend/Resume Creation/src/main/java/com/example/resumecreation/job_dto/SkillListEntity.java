package com.example.resumecreation.job_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SkillListEntity {

    public Long id;
    public String name;

    private SkillTypeEntity skillTypeEntity;

    public SkillListEntity(String name) {
        this.name = name;
    }


}
