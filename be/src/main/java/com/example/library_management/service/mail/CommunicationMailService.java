package com.example.library_management.service.mail;

public class CommunicationMailService {
    private final String html_footer = """
    <div style="margin-top:20px; margin-bottom:20px;padding-top:18px;text-align:right; margin-right: 20px;">
        <small>Library Management Team</small>
    </div>
    <div style="background:#F8FAFC; padding:18px 25px; text-align:center; border-top:1px solid #E5E7EB;">
        <p style="margin:0; font-size:11px; color:#9CA3AF; ">This is an automated message. Please do not reply to this email.</p>
    </div>
    """;

    private final String html_header_multi_lang = """
    <div style="background:#2C5EAD;padding:28px 25px; text-align:center;">
        <h1 style="color:#EAEFEF;margin:0;font-size:24px;font-weight:600;">
            Library Management System
        </h1>
    </div>
    <!-- Language navigation -->
    <div style="padding:16px 25px;background:#F8FAFC;border-bottom:1px solid #E5E7EB;text-align:center;">
        <span style="font-size:12px;color:#6B7280;">Tiếng Việt</span>
        <span style="color:#CBD5E1; margin:0 10px;"> • </span>
        <span style="font-size:12px;color:#6B7280;">English</span>
        <span style="color:#CBD5E1; margin:0 10px;"> • </span>
        <span style="font-size:12px; color:#6B7280;">Français</span>
    </div>
    """;
}
