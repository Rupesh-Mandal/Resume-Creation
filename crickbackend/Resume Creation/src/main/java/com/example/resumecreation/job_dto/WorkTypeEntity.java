package com.example.resumecreation.job_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class WorkTypeEntity {

    public Long id;
    public String name;
    public String name_fr;

    public WorkTypeEntity(Long id) {
        this.id = id;
    }
}
