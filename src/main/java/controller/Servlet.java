package controller;

import data.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/emailList")
public class Servlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");

        User user = new User(firstName, lastName, email);
        String message;
        String url;

        // Gọi UserDAO kiểm tra email
        if (UserDAO.emailExists(email)) {
            message = "This email address already exists.<br>Please enter another email address.";
            url = "/index.jsp";
        } else {
            message = "";
            UserDAO.insert(user);

            //CHƯƠNG 14: GỬI EMAIL CHÀO MỪNG QUA BREVO API ---
            String to = email;
            // from sẽ bị override bởi BREVO_FROM_EMAIL env var trong MailUtil
            String from = "";
            String subject = "Welcome to our email list!";
            String body = "Dear " + firstName + ",\n\n"
                    + "Thanks for joining our email list. We'll keep you updated!\n\n"
                    + "Best regards,\nAdmin Team";
            boolean bodyIsHTML = false;

            try {
                util.MailUtil.sendMail(to, from, subject, body, bodyIsHTML);
            } catch (IOException | InterruptedException e) {
                // Gửi mail lỗi → vẫn chuyển thanks.jsp, ghi log để debug
                System.err.println("[MailUtil] Failed to send mail to " + to + ": " + e.getMessage());
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt(); // restore interrupted status
                }
            }

            url = "/thanks.jsp";
        }

        request.setAttribute("user", user);
        request.setAttribute("message", message);

        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}