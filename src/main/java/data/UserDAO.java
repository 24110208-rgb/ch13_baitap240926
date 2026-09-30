package data;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import model.User;

public class UserDAO {

    // Khởi tạo bộ quản lý Factory nạp từ cấu hình xml [1]=
    private static final EntityManagerFactory emf = 
            Persistence.createEntityManagerFactory("murachPU");

    // 1. Kỹ thuật THÊM MỚI dữ liệu bằng hàm persist có sẵn [1]
    public static int insert(User user) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction trans = em.getTransaction();
        
        try {
            trans.begin(); // Mở giao dịch [1]
            em.persist(user); // Lưu thẳng Object xuống Database không cần viết câu lệnh SQL [1]
            trans.commit(); // Lưu thay đổi [1]
            return 1;
        } catch (Exception e) {
            if (trans.isActive()) {
                trans.rollback(); // Hoàn tác nếu lỗi [1]
            }
            e.printStackTrace();
            return 0;
        } finally {
            em.close(); // Đóng tài nguyên an toàn [1]
        }
    }

    // 2. Kỹ thuật TÌM KIẾM/KIỂM TRA tồn tại bằng JPQL (Truy vấn hướng đối tượng) [1]
    public static boolean emailExists(String email) {
        EntityManager em = emf.createEntityManager();
        
        // Viết câu lệnh dựa trên Class User chứ không viết dựa trên tên bảng dưới SQL [1]
        String qString = "SELECT u FROM User u WHERE u.email = :email";
        TypedQuery<User> q = em.createQuery(qString, User.class);
        q.setParameter("email", email); // Chống SQL Injection tuyệt đối [1]
        
        try {
            User user = q.getSingleResult(); // Lấy duy nhất 1 kết quả trả về [1]
            return user != null;
        } catch (jakarta.persistence.NoResultException e) {
            // JPA sẽ quăng Exception này nếu không tìm thấy dữ liệu phù hợp [1]
            return false; 
        } finally {
            em.close(); // Luôn luôn đóng tài nguyên [1]
        }
    }
    
    // Kỹ thuật bổ sung: Lấy toàn bộ thông tin User đầy đủ bằng ID (Hàm find tự động) [1]
    public static User selectUser(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            // Hàm find tự động map đầy đủ cột thành một thực thể User [1]
            return em.find(User.class, email); 
        } finally {
            em.close();
        }
    }
}
