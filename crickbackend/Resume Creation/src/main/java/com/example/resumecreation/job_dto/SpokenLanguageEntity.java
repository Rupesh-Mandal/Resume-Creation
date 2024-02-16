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
public class SpokenLanguageEntity {

    public Long id;
    public String name;
    public String nameFr;

    @JsonIgnore
    private Long jobId;
}
