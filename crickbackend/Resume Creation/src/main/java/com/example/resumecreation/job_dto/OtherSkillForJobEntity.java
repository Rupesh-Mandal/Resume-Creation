package com.example.resumecreation.job_dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OtherSkillForJobEntity {

    public Long id;
    public String name;

    @JsonIgnore
    private Long jobId;

    public OtherSkillForJobEntity(String name, Long jobId) {
        this.name = name;
        this.jobId = jobId;
    }
}
