package util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Gửi email qua Resend API (HTTPS) - https://resend.com
 * Không dùng SMTP → tránh bị block trên các host free (Render, Railway...).
 *
 * Biến môi trường cần set trên Render:
 *   RESEND_API_KEY — API key lấy từ resend.com → API Keys
 *   RESEND_FROM    — địa chỉ gửi, dùng "onboarding@resend.dev" nếu chưa có domain riêng
 *                    (chỉ gửi được đến email đã đăng ký tài khoản Resend)
 */
public class MailUtil {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    public static void sendMail(String to, String from, String subject, String body, boolean bodyIsHTML)
            throws IOException, InterruptedException {

        // Đọc config từ biến môi trường
        String apiKey   = System.getenv("RESEND_API_KEY");
        String fromAddr = System.getenv("RESEND_FROM");

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Biến môi trường RESEND_API_KEY chưa được set.");
        }
        // Nếu chưa set RESEND_FROM, dùng địa chỉ test mặc định của Resend
        if (fromAddr == null || fromAddr.isBlank()) {
            fromAddr = "onboarding@resend.dev";
        }

        // Escape ký tự đặc biệt để nhúng vào JSON an toàn
        String safeSubject = escapeJson(subject);
        String safeBody    = escapeJson(body);
        String safeTo      = escapeJson(to);
        String safeFrom    = escapeJson(fromAddr);

        // Xây dựng JSON body theo Resend API
        String contentKey = bodyIsHTML ? "html" : "text";
        String jsonBody = "{"
                + "\"from\":\"" + safeFrom + "\","
                + "\"to\":[\"" + safeTo + "\"],"
                + "\"subject\":\"" + safeSubject + "\","
                + "\"" + contentKey + "\":\"" + safeBody + "\""
                + "}";

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RESEND_API_URL))
                .timeout(Duration.ofSeconds(15))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        // Resend trả 200 hoặc 201 khi thành công
        if (statusCode != 200 && statusCode != 201) {
            throw new IOException("Resend API trả lỗi " + statusCode + ": " + response.body());
        }

        System.out.println("[MailUtil] Email gửi thành công đến " + to + " (HTTP " + statusCode + ")");
    }

    /**
     * Escape các ký tự đặc biệt trong chuỗi để nhúng vào JSON an toàn.
     */
    private static String escapeJson(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
