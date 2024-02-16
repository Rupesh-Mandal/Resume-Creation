package com.example.resumecreation.job_dto;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobDto {

    private Long jobId;

    public String positionTitle;
    public String title;
    public String description;
    public String onOfPosition;
    private String language;
    private Boolean isOtherProfile;
    public Long totalExperience;
    public Long postalCode;
    public Boolean certificationsLicensesIsRequired;
    private List<SkillListEntity> skillListEntities;
    private String certificateName;
    public Long createdBy;
    public Long updatedBy;

    public Boolean isInternal=false;
    public Long clientId;
    public String recruitmentQuota;
    public Boolean isFromAi;
    public Long totalInvited;
    public Long totalApplied;
    public Long totalInterview;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
    private List<JobRequirementEntity> jobRequirementEntities;
    private List<JobKeyResponsibilityEntity> jobKeyResponsibilityEntities;

    List<OtherSkillForJobEntity> otherSkills;

    public Long qualifiedCount;
    public Long interviewedCount;
    public Long financialNegotiationCount;
    public Long sentToClintCount;
    public Long hiredCount;

}
