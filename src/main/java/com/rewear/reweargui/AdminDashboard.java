
package com.rewear.reweargui;

import com.rewear.reweargui.database.DatabaseConnection;
import com.rewear.reweargui.database.BarangDAO;
import com.rewear.reweargui.model.BarangItem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class AdminDashboard {

    private final Stage stage;
    private final Runnable aksiLogout;
    private final BarangDAO barangDAO = new BarangDAO();

    private final String NAVY = "#142D4E";
    private final String BLUE = "#2878C7";
    private final String BG = "#F4F7FB";
    private final String BORDER = "#DCE5EF";

    private final NumberFormat rupiah =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    public AdminDashboard(Stage stage, Runnable aksiLogout) {
        this.stage = stage;
        this.aksiLogout = aksiLogout;
    }

    public void tampilkan() {

        Label logo = new Label("REWEAR ADMIN");
        logo.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 23));
        logo.setTextFill(Color.WHITE);

        Button dashboard = menu("Dashboard");
        Button pengguna = menu("Data Pengguna");
        Button barang = menu("Data Barang");
        Button logout = menu("Logout");

        Region ruang = new Region();
        VBox.setVgrow(ruang, Priority.ALWAYS);

        VBox sidebar = new VBox(
                20, logo, new Separator(),
                dashboard, pengguna, barang, ruang, logout
        );

        sidebar.setPadding(new Insets(25, 18, 20, 18));
        sidebar.setPrefWidth(225);
        sidebar.setStyle("-fx-background-color: " + NAVY + ";");

        dashboard.setOnAction(e -> tampilkan());
        pengguna.setOnAction(e -> tampilkanPengguna());
        barang.setOnAction(e -> tampilkanBarang());
        logout.setOnAction(e -> aksiLogout.run());

        BorderPane layout = new BorderPane();
        layout.setLeft(sidebar);
        layout.setCenter(kontenDashboard());

        stage.setScene(new Scene(layout, 1250, 780));
        stage.setTitle("ReWear | Admin Dashboard");
        stage.show();
    }

    private VBox kontenDashboard() {

        Label judul = judul("Dashboard Admin", 25);
        Label sub = new Label("Ringkasan aktivitas marketplace ReWear.");

        Label totalUser = new Label("0");
        Label totalBarang = new Label("0");
        Label tersedia = new Label("0");
        Label terjual = new Label("0");

        try (Connection conn = DatabaseConnection.getConnection()) {

            totalUser.setText(String.valueOf(hitung(conn,
                    "SELECT COUNT(*) FROM users")));

            totalBarang.setText(String.valueOf(hitung(conn,
                    "SELECT COUNT(*) FROM barang")));

            tersedia.setText(String.valueOf(hitung(conn,
                    "SELECT COUNT(*) FROM barang WHERE status='TERSEDIA'")));

            terjual.setText(String.valueOf(hitung(conn,
                    "SELECT COUNT(*) FROM barang WHERE status='TERJUAL'")));

        } catch (Exception ex) {
            alert("Gagal Memuat Dashboard", ex.getMessage());
        }

        HBox statistik = new HBox(15,
                kartuStat("TOTAL PENGGUNA", totalUser),
                kartuStat("TOTAL BARANG", totalBarang),
                kartuStat("TERSEDIA", tersedia),
                kartuStat("TERJUAL", terjual)
        );

        Label info = judul("Selamat Datang, Admin!", 20);
        Label detail = new Label(
                "Kelola pengguna dan pantau barang yang dipasarkan di ReWear."
        );

        VBox konten = new VBox(22, judul, sub, statistik, info, detail);
        konten.setPadding(new Insets(30));
        konten.setStyle("-fx-background-color: " + BG + ";");

        return konten;
    }

    private void tampilkanPengguna() {

        Label judul = judul("Data Pengguna", 25);

        TableView<String[]> tabel = new TableView<>();

        tambahKolom(tabel, "ID", 60, 0);
        tambahKolom(tabel, "Nama", 180, 1);
        tambahKolom(tabel, "Username", 150, 2);
        tambahKolom(tabel, "WhatsApp", 160, 3);
        tambahKolom(tabel, "Role", 100, 4);

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT id_user, nama, username, nomor_whatsapp, role "
                     + "FROM users ORDER BY id_user DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                tabel.getItems().add(new String[]{
                    String.valueOf(rs.getInt("id_user")),
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("nomor_whatsapp"),
                    rs.getString("role")
                });
            }

        } catch (Exception ex) {
            alert("Gagal Memuat Pengguna", ex.getMessage());
        }

        Button kembali = tombol("KEMBALI");
        kembali.setOnAction(e -> tampilkan());

        VBox konten = new VBox(18, judul, tabel, kembali);
        konten.setPadding(new Insets(30));
        konten.setStyle("-fx-background-color: " + BG + ";");
        VBox.setVgrow(tabel, Priority.ALWAYS);

        stage.setScene(new Scene(konten, 1150, 750));
    }

    private void tampilkanBarang() {

        Label judul = judul("Data Barang Marketplace", 25);

        TableView<BarangItem> tabel = new TableView<>();

        TableColumn<BarangItem, String> nama =
                new TableColumn<>("Nama Barang");
        nama.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNama()));

        TableColumn<BarangItem, String> penjual =
                new TableColumn<>("Penjual");
        penjual.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getNamaPenjual()));

        TableColumn<BarangItem, String> harga =
                new TableColumn<>("Harga");
        harga.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        rupiah.format(data.getValue().getHarga())));

        TableColumn<BarangItem, String> status =
                new TableColumn<>("Status");
        status.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getStatus()));

        tabel.getColumns().addAll(nama, penjual, harga, status);

        try {
            List<BarangItem> daftar = barangDAO.getSemuaBarang();
            tabel.getItems().setAll(daftar);
        } catch (Exception ex) {
            alert("Gagal Memuat Barang", ex.getMessage());
        }

        Button hapus = tombol("HAPUS BARANG");
        Button kembali = tombol("KEMBALI");

        hapus.setOnAction(e -> {

            BarangItem pilihan = tabel.getSelectionModel().getSelectedItem();

            if (pilihan == null) {
                alert("Pilih Barang", "Pilih barang yang ingin dihapus.");
                return;
            }

            Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
            konfirmasi.setTitle("Konfirmasi");
            konfirmasi.setHeaderText("Hapus " + pilihan.getNama() + "?");
            konfirmasi.setContentText("Barang akan dihapus dari marketplace.");

            if (konfirmasi.showAndWait().orElse(ButtonType.CANCEL)
                    == ButtonType.OK) {

                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "DELETE FROM barang WHERE id_barang=?")) {

                    ps.setInt(1, pilihan.getIdBarang());
                    ps.executeUpdate();

                    alert("Berhasil", "Barang berhasil dihapus.");
                    tampilkanBarang();

                } catch (Exception ex) {
                    alert("Gagal Menghapus", ex.getMessage());
                }
            }
        });

        kembali.setOnAction(e -> tampilkan());

        HBox tombol = new HBox(12, hapus, kembali);

        VBox konten = new VBox(18, judul, tabel, tombol);
        konten.setPadding(new Insets(30));
        konten.setStyle("-fx-background-color: " + BG + ";");
        VBox.setVgrow(tabel, Priority.ALWAYS);

        stage.setScene(new Scene(konten, 1150, 750));
    }

    private int hitung(Connection conn, String sql) throws SQLException {

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getInt(1);
        }

        return 0;
    }

    private void tambahKolom(TableView<String[]> tabel,
            String nama, double lebar, int indeks) {

        TableColumn<String[], String> kolom =
                new TableColumn<>(nama);

        kolom.setPrefWidth(lebar);

        kolom.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue()[indeks]));

        tabel.getColumns().add(kolom);
    }

    private VBox kartuStat(String nama, Label angka) {

        Label label = new Label(nama);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        angka.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 27));
        angka.setTextFill(Color.web(BLUE));

        VBox kartu = new VBox(12, label, angka);
        kartu.setPadding(new Insets(20));
        kartu.setPrefWidth(210);
        kartu.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 12;"
                + "-fx-border-color: " + BORDER + ";"
                + "-fx-border-radius: 12;"
        );

        return kartu;
    }

    private Label judul(String teks, int ukuran) {

        Label label = new Label(teks);
        label.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, ukuran));
        label.setTextFill(Color.web(NAVY));

        return label;
    }

    private Button menu(String teks) {

        Button button = new Button(teks);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(43);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle(
                "-fx-background-color: " + NAVY + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-cursor: hand;"
        );

        return button;
    }

    private Button tombol(String teks) {

        Button button = new Button(teks);
        button.setPrefHeight(40);
        button.setStyle(
                "-fx-background-color: " + BLUE + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 8;"
        );

        return button;
    }

    private void alert(String judul, String pesan) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ReWear Admin");
        alert.setHeaderText(judul);
        alert.setContentText(pesan);
        alert.showAndWait();
    }
}
