package com.example.resumecreation;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeDataDto implements Serializable {
    public Boolean isPersonalDetails;

    public String jobTitle;
    public Boolean isJobTitle;
    private String fullName = null;
    private Boolean isFullName = null;

    private String email = null;
    private Boolean isEmail = false;

    private String companyName = null;
    private Boolean isCompanyName = false;

    private Boolean useCompanyLogo=false;

    private String mobilePhone;
    private Boolean isMobilePhone=false;
    private boolean hasDrivingLicience;
    private boolean hasWorkPermit;

    private String mobilityArea;

    private String address = null;
    private Boolean isAddress = false;
    private Integer uuid;

    private Double scoreV=0.0;
    public Boolean isScoreV;

    private List<CandidateSkillEntity> skillListEntities;
    public Boolean isSkillListEntities;
    public Boolean isOtherSkill;
    public String otherSkill;

    private List<CandidateEducationEntity> candidateEducationEntities;
    private Boolean isCandidateEducationEntities;

    private List<CandidateCertificateEntity> candidateCertificateEntities;
    private Boolean isCandidateCertificateEntities;

    private List<ProjectPortfolioEntity> projectPortfolioEntities;
    private Boolean isProjectPortfolioEntities;

    private List<WorkExperienceEntity> workExperienceEntities;
    private Boolean isWorkExperienceEntities;

    private List<SocialAccountEntity> socialAccountEntities;

    public List<LanguageEntity> languageEntities;
    public Boolean isLanguageEntities;

    public Long totalExperience;
    public Boolean isTotalExperience;

    public String salaryCurrency;
    public String aboutContent;
    public Boolean isAboutContent;
    public String stage;
    public String pincode;
    public String SSIN;
    public String currency;
    public String currentCTC;
    public String desiredCTC;
    public String agencyName;
    public String managerName;
    public String civility;
    public String nationality;
    public Boolean isNationality=false;
    public String availability;
    public Boolean isAvailability;
    public String desiredWorkType;
    public Boolean isDesiredWorkType;
    public String desiredPosition;
    public Boolean isEmployee=false;

    private EmploymentTypeEntity employmentTypeEntity;
    public Boolean isEmploymentTypeEntity;


    private CountryEntity countryEntity;

    private StateEntity stateEntity;

    private CityEntity cityEntity;

    public String location;
    public Boolean isLocation;


    public String  dob;


    private String photo;
    private Boolean createdWithCv=false;

    private boolean isAccepted;

    private String comment;

    private String statut;
    private String administrationStatus;

    public List<CustomDetails> customDetails;
    public Boolean isCustomDetails;

    public Integer evaluation;
    public Boolean isEvaluation;

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CustomDetails implements Serializable{
        public String fieldName;
        public String activityName;
        public String description;
    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkExperienceEntity implements Serializable {
        public String jobTitle;
        public Boolean isJobTitle;

        public String companyName;
        public Boolean isCompanyName;
        public String description;
        public Boolean isDescription;
        public String startDate;
        public Boolean isStartDate;
        public String  endDate;
        public Boolean isEndDate;

        public String employmentType;
        public Boolean isEmploymentType;
        public String location;
        public Boolean isLocation;
        public String workType;
        public Boolean isWorkType;

    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProjectPortfolioEntity implements Serializable {
        public String projectTitle;
        public Boolean isProjectTitle;
        public String projectDescription;
        public Boolean isProjectDescription;

    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandidateEducationEntity implements Serializable {

        private String degreeName;
        private Boolean isDegreeName;
        private String college;
        private Boolean isCollege;
        private String year;
        private Boolean isYear;

    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandidateCertificateEntity implements Serializable {

        private String name;
        private Boolean isName;
        private String institution;
        private Boolean isInstitution;
        private String year;
        private Boolean isYear;



    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LanguageEntity implements Serializable {

        private String name;
        private Boolean isName;
        private String proficiency;
        private Boolean isProficiency;


    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CandidateSkillEntity implements Serializable{

        public Long id;
        public String name;
        public String type;
        public Integer nExperience;
        public String description;
        public String evaluation;
    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SocialAccountEntity implements Serializable{
        public String name;
        public String profileUrl;
    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EmploymentTypeEntity implements Serializable{

        public Long id;
        public String name;
        public String name_fr;
    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CountryEntity implements Serializable{
        private int countryId;
        private String name;

    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StateEntity implements Serializable {
        private int stateId;
        private String name;
        private int countryId;
    }

    @Data
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CityEntity implements Serializable {
        private int cityId;

        private String name;
        private int stateId;
    }
}

