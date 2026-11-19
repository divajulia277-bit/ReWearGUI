package com.rewear.reweargui.database;

import com.rewear.reweargui.model.BarangItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    public boolean tambahBarang(int idUser, String nama,
            String kategori, double harga, String kondisi,
            String ukuran, String merek, String foto) throws Exception {

        String sql = "INSERT INTO barang "
                + "(id_user, nama_barang, kategori, harga, kondisi, ukuran, merek, status, foto) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, 'TERSEDIA', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUser);
            ps.setString(2, nama);
            ps.setString(3, kategori);
            ps.setDouble(4, harga);
            ps.setString(5, kondisi);
            ps.setString(6, ukuran);
            ps.setString(7, merek);
            ps.setString(8, foto);

            return ps.executeUpdate() > 0;
        }
    }

    public List<BarangItem> getSemuaBarang() throws Exception {
        return ambilBarang(false, 0);
    }

    public List<BarangItem> getBarangMilikUser(int idUser) throws Exception {
        return ambilBarang(true, idUser);
    }

    private List<BarangItem> ambilBarang(boolean milikUser, int idUser)
            throws Exception {

        List<BarangItem> daftar = new ArrayList<>();

        String sql = "SELECT b.*, u.nama AS nama_penjual, "
                + "u.nomor_whatsapp AS whatsapp_penjual "
                + "FROM barang b JOIN users u ON b.id_user = u.id_user "
                + (milikUser ? "WHERE b.id_user = ? " : "")
                + "ORDER BY b.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (milikUser) {
                ps.setInt(1, idUser);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    daftar.add(new BarangItem(
                            rs.getInt("id_barang"),
                            rs.getInt("id_user"),
                            rs.getString("nama_barang"),
                            rs.getString("kategori"),
                            rs.getDouble("harga"),
                            rs.getString("kondisi"),
                            rs.getString("ukuran"),
                            rs.getString("merek"),
                            rs.getString("status"),
                            rs.getString("nama_penjual"),
                            rs.getString("foto"),
                            rs.getString("whatsapp_penjual")
                    ));
                }
            }
        }

        return daftar;
    }

    public int hitungBarang(String status) throws Exception {
        String sql = status == null
                ? "SELECT COUNT(*) FROM barang"
                : "SELECT COUNT(*) FROM barang WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (status != null) {
                ps.setString(1, status);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }

        return 0;
    }

    public boolean updateBarang(int idBarang, int idUser,
            String nama, String kategori, double harga,
            String kondisi, String ukuran, String merek,
            String foto) throws Exception {

        String sql = "UPDATE barang SET nama_barang=?, kategori=?, harga=?, "
                + "kondisi=?, ukuran=?, merek=?, foto=? "
                + "WHERE id_barang=? AND id_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nama);
            ps.setString(2, kategori);
            ps.setDouble(3, harga);
            ps.setString(4, kondisi);
            ps.setString(5, ukuran);
            ps.setString(6, merek);
            ps.setString(7, foto);
            ps.setInt(8, idBarang);
            ps.setInt(9, idUser);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean hapusBarang(int idBarang, int idUser)
            throws Exception {

        String sql = "DELETE FROM barang WHERE id_barang=? AND id_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idBarang);
            ps.setInt(2, idUser);

            return ps.executeUpdate() > 0;
        }
    }

    public boolean ubahStatusBarang(int idBarang, int idUser,
            String status) throws Exception {

        String sql = "UPDATE barang SET status=? "
                + "WHERE id_barang=? AND id_user=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, idBarang);
            ps.setInt(3, idUser);

            return ps.executeUpdate() > 0;
        }
    }
}