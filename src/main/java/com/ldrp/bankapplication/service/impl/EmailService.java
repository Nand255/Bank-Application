package com.ldrp.bankapplication.service.impl;

import com.ldrp.bankapplication.dto.EmailDetails;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);
    void sendEmailWithAttachment(EmailDetails emailDetails);
}
