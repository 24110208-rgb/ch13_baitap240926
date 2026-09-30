package data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserDAO {

    // Khởi tạo EMF một lần duy nhất, đọc DB config từ biến môi trường
    private static final EntityManagerFactory emf = createEMF();

    private static EntityManagerFactory createEMF() {
        // Đọc biến môi trường, fallback về giá trị local nếu không có
        String dbUrl  = getEnv("DB_URL",      "jdbc:mysql://localhost:3306/murach");
        String dbUser = getEnv("DB_USER",     "root");
        String dbPass = getEnv("DB_PASSWORD", "123456");

        Map<String, String> props = new HashMap<>();
        props.put("jakarta.persistence.jdbc.url",      dbUrl);
        props.put("jakarta.persistence.jdbc.user",     dbUser);
        props.put("jakarta.persistence.jdbc.password", dbPass);
        props.put("jakarta.persistence.jdbc.driver",   "com.mysql.cj.jdbc.Driver");

        return Persistence.createEntityManagerFactory("murachPU", props);
    }

    private static String getEnv(String key, String fallback) {
        String val = System.getenv(key);
        return (val != null && !val.isBlank()) ? val : fallback;
    }

    // 1. Thêm mới User
    public static int insert(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trans = em.getTransaction();
        try {
            trans.begin();
            em.persist(user);
            trans.commit();
            return 1;
        } catch (Exception e) {
            if (trans.isActive()) trans.rollback();
            e.printStackTrace();
            return 0;
        } finally {
            em.close();
        }
    }

    // 2. Kiểm tra email đã tồn tại chưa
    public static boolean emailExists(String email) {
        EntityManager em = emf.createEntityManager();
        String qString = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> q = em.createQuery(qString, User.class);
        q.setParameter("email", email);
        try {
            User user = q.getSingleResult();
            return user != null;
        } catch (jakarta.persistence.NoResultException e) {
            return false;
        } finally {
            em.close();
        }
    }

    // 3. Lấy User theo email
    public static User selectUser(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(User.class, email);
        } finally {
            em.close();
        }
    }
}
