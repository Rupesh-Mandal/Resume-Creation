package com.example.resumecreation.resume_service;

import com.example.resumecreation.ResumeDataDto;
import com.example.resumecreation.utils.Constant;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public interface ResumeBuilderService {
    ByteArrayOutputStream generateResume(Long jobId, Long candidateId, ResumeDataDto resumeDataDto, Long idCompany, Constant.LanguageKey languageKey) throws IOException;

}
