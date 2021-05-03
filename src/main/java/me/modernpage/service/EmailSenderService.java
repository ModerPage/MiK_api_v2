package me.modernpage.service;

import me.modernpage.entity.ConfirmationToken;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

public interface EmailSenderService {
    void sendEmail(ConfirmationToken confirmationToken, String url) throws MessagingException, UnsupportedEncodingException;
}
