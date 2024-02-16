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
public class JobKeyResponsibilityEntity {

    private Long id;
    private String name;

    public JobKeyResponsibilityEntity(String name) {
        this.name = name;
    }
}
