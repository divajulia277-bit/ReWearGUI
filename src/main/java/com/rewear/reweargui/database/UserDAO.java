
package com.rewear.reweargui.database;

import com.rewear.reweargui.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public boolean usernameSudahTerdaftar(String username)
            throws SQLException {

        String sql = "SELECT id_user FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean registerUser(
            String nama,
            String username,
            String password,
            String nomorWhatsapp) throws SQLException {

        String sql = "INSERT INTO users "
                + "(nama, username, password, nomor_whatsapp) "
                + "VALUES (?, ?, ?, ?)";

        String passwordHash = PasswordUtil.hashPassword(password);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, username);
            ps.setString(3, passwordHash);
            ps.setString(4, nomorWhatsapp);

            return ps.executeUpdate() > 0;
        }
    }

    public User loginUser(String username, String password)
            throws SQLException {

        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    String passwordDB = rs.getString("password");

                    if (PasswordUtil.verifyPassword(
                            password, passwordDB)) {

                        return new User(
                                rs.getInt("id_user"),
                                rs.getString("nama"),
                                rs.getString("username"),
                                passwordDB,
                                rs.getString("nomor_whatsapp"),
                                rs.getString("role")
                        );
                    }
                }
            }
        }

        return null;
    }
}
