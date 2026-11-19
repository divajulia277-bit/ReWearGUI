
package com.rewear.reweargui;

import com.rewear.reweargui.database.UserDAO;
import com.rewear.reweargui.database.BarangDAO;
import com.rewear.reweargui.model.User;
import com.rewear.reweargui.model.BarangItem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class ReWearGUI extends Application {

    private Stage stage;

    private final UserDAO userDAO = new UserDAO();
    private final BarangDAO barangDAO = new BarangDAO();

    // WARNA MODERN REWEAR
    private final String NAVY = "#142D4E";
    private final String DARK_BLUE = "#1D426B";
    private final String BLUE = "#2878C7";
    private final String LIGHT_BLUE = "#EAF2FA";
    private final String BACKGROUND = "#F4F7FB";
    private final String TEXT = "#263B53";
    private final String MUTED = "#64748B";
    private final String BORDER = "#DCE5EF";

    private final NumberFormat rupiah =
        NumberFormat.getCurrencyInstance(
            new Locale("id", "ID")
        );

    private FlowPane areaProduk;
    private TextField pencarian;

    private Label totalBarang;
    private Label barangTersedia;
    private Label barangTerjual;

    private List<BarangItem> semuaBarang = new ArrayList<>();
    private User userAktif;

    @Override
    public void start(Stage stage) {

        this.stage = stage;

        tampilkanLogin();

        stage.setTitle("ReWear | Preloved Marketplace");
        stage.setMinWidth(1050);
        stage.setMinHeight(680);
        stage.show();
    }

    // =================================
    // LOGIN
    // =================================

    private void tampilkanLogin() {

        Label logo = buatJudul("REWEAR", 36);

        Label tagline = new Label(
            "PRELOVED MARKETPLACE"
        );

        tagline.setStyle(
            "-fx-text-fill: " + BLUE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-font-size: 12px;"
        );

        TextField username = new TextField();
        username.setPromptText("Masukkan username");
        aturInput(username);

        PasswordField password = new PasswordField();
        password.setPromptText("Masukkan password");
        aturInput(password);

        Button login = buatTombol("LOGIN");

        Hyperlink daftar = new Hyperlink(
            "Belum punya akun? Daftar sekarang"
        );

        VBox form = new VBox(
            15,
            logo,
            tagline,
            new Separator(),
            buatJudul("Welcome Back", 25),
            buatLabel("USERNAME"),
            username,
            buatLabel("PASSWORD"),
            password,
            login,
            daftar
        );

        form.setPadding(new Insets(38));
        form.setMaxWidth(430);

        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;"
        );

        VBox halaman = new VBox(form);

        halaman.setAlignment(Pos.CENTER);
        halaman.setPadding(new Insets(25));

        halaman.setStyle(
            "-fx-background-color: " + BACKGROUND + ";"
        );

        login.setOnAction(e ->
            prosesLogin(username.getText(), password.getText())
        );

        daftar.setOnAction(e -> tampilkanRegister());

        stage.setScene(
            new Scene(halaman, 1150, 720)
        );
    }

    // =================================
    // REGISTER
    // =================================

    private void tampilkanRegister() {

        Label judul = buatJudul("CREATE ACCOUNT", 27);

        Label subjudul = new Label(
            "Bergabung dan mulai jual beli barang preloved."
        );

        subjudul.setTextFill(Color.web(MUTED));

        TextField nama = new TextField();
        nama.setPromptText("Nama lengkap");
        aturInput(nama);

        TextField username = new TextField();
        username.setPromptText("Buat username");
        aturInput(username);

        TextField whatsapp = new TextField();
        whatsapp.setPromptText("Nomor WhatsApp");
        aturInput(whatsapp);

        PasswordField password = new PasswordField();
        password.setPromptText("Minimal 8 karakter");
        aturInput(password);

        PasswordField konfirmasi = new PasswordField();
        konfirmasi.setPromptText("Ulangi password");
        aturInput(konfirmasi);

        Button daftar = buatTombol("DAFTAR SEKARANG");

        Hyperlink kembali = new Hyperlink(
            "Sudah punya akun? Login"
        );

        VBox form = new VBox(
            10,
            judul,
            subjudul,
            buatLabel("NAMA LENGKAP"),
            nama,
            buatLabel("USERNAME"),
            username,
            buatLabel("NOMOR WHATSAPP"),
            whatsapp,
            buatLabel("PASSWORD"),
            password,
            buatLabel("KONFIRMASI PASSWORD"),
            konfirmasi,
            daftar,
            kembali
        );

        form.setPadding(new Insets(32));
        form.setMaxWidth(440);

        form.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 14;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 14;"
        );

        VBox halaman = new VBox(form);

        halaman.setAlignment(Pos.CENTER);
        halaman.setPadding(new Insets(25));

        halaman.setStyle(
            "-fx-background-color: " + BACKGROUND + ";"
        );

        daftar.setOnAction(e -> {

            String n = nama.getText().trim();
            String un = username.getText().trim();
            String wa = whatsapp.getText().trim();
            String pw = password.getText();
            String kon = konfirmasi.getText();

            if (n.isEmpty() || un.isEmpty()
                    || wa.isEmpty() || pw.isEmpty()
                    || kon.isEmpty()) {

                tampilkanAlert(
                    Alert.AlertType.WARNING,
                    "Data Belum Lengkap",
                    "Mohon lengkapi seluruh formulir."
                );

                return;
            }

            if (!un.matches("[A-Za-z0-9_]{4,20}")) {

                tampilkanAlert(
                    Alert.AlertType.WARNING,
                    "Username Tidak Valid",
                    "Username harus 4-20 karakter dan hanya boleh menggunakan huruf, angka, atau underscore."
                );

                return;
            }

            if (pw.length() < 8) {

                tampilkanAlert(
                    Alert.AlertType.WARNING,
                    "Password Tidak Valid",
                    "Password minimal 8 karakter."
                );

                return;
            }

            if (!pw.equals(kon)) {

                tampilkanAlert(
                    Alert.AlertType.WARNING,
                    "Password Tidak Sama",
                    "Konfirmasi password tidak cocok."
                );

                return;
            }

            if (!wa.matches("[0-9]{10,15}")) {

                tampilkanAlert(
                    Alert.AlertType.WARNING,
                    "Nomor Tidak Valid",
                    "Nomor WhatsApp harus 10-15 digit."
                );

                return;
            }

            try {

                if (userDAO.usernameSudahTerdaftar(un)) {

                    tampilkanAlert(
                        Alert.AlertType.WARNING,
                        "Username Sudah Digunakan",
                        "Silakan gunakan username lain."
                    );

                    return;
                }

                boolean berhasil = userDAO.registerUser(
                    n, un, pw, wa
                );

                if (berhasil) {

                    tampilkanAlert(
                        Alert.AlertType.INFORMATION,
                        "Registrasi Berhasil",
                        "Akun berhasil dibuat. Silakan login."
                    );

                    tampilkanLogin();
                }

            } catch (SQLException ex) {

                tampilkanAlert(
                    Alert.AlertType.ERROR,
                    "Registrasi Gagal",
                    ex.getMessage()
                );
            }
        });

        kembali.setOnAction(e -> tampilkanLogin());

        stage.setScene(
            new Scene(halaman, 1150, 720)
        );
    }

    // =================================
    // PROSES LOGIN
    // =================================

    private void prosesLogin(
        String username,
        String password
    ) {

        if (username.trim().isEmpty()
                || password.isEmpty()) {

            tampilkanAlert(
                Alert.AlertType.WARNING,
                "Login Gagal",
                "Username dan password harus diisi."
            );

            return;
        }

        try {

            User user = userDAO.loginUser(
                username.trim(), password
            );

            if (user == null) {

                tampilkanAlert(
                    Alert.AlertType.ERROR,
                    "Login Gagal",
                    "Username atau password salah."
                );

                return;
            }

            if ("ADMIN".equalsIgnoreCase(user.getRole())) {

    AdminDashboard admin = new AdminDashboard(
        stage,
        this::tampilkanLogin
    );

    admin.tampilkan();

} else {

    tampilkanDashboard(user);

}

        } catch (SQLException ex) {

            tampilkanAlert(
                Alert.AlertType.ERROR,
                "Database Error",
                ex.getMessage()
            );
        }
    }

    // =================================
    // DASHBOARD MARKETPLACE
    // =================================
    private void tampilkanDashboard(User user) {

        userAktif = user;

        // SIDEBAR
        Label logo = buatJudulPutih("REWEAR", 25);

        Label keterangan = new Label(
            "PRELOVED MARKETPLACE"
        );

        keterangan.setStyle(
            "-fx-text-fill: #B9CDE2;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;"
        );

        VBox identitas = new VBox(5, logo, keterangan);

        Button beranda = tombolMenu("Beranda", true);
        Button jelajahi = tombolMenu("Jelajahi", false);
        Button jual = tombolMenu("Jual Barang", false);
        Button barangSaya = tombolMenu("Barang Saya", false);
        Button profil = tombolMenu("Profil", false);

        Region ruang = new Region();
        VBox.setVgrow(ruang, Priority.ALWAYS);

        Button logout = tombolMenu("Logout", false);

        VBox sidebar = new VBox(
            20,
            identitas,
            new Separator(),
            beranda,
            jelajahi,
            jual,
            barangSaya,
            profil,
            ruang,
            logout
        );

        sidebar.setPadding(
            new Insets(27, 18, 22, 18)
        );

        sidebar.setPrefWidth(225);

        sidebar.setStyle(
            "-fx-background-color: " + NAVY + ";"
        );

        // HEADER
        Label sapaan = buatJudul(
            "Halo, " + user.getNama() + "!", 24
        );

        Label deskripsi = new Label(
            "Temukan barang preloved favoritmu hari ini."
        );

        deskripsi.setTextFill(Color.web(MUTED));

        VBox judul = new VBox(5, sapaan, deskripsi);

        pencarian = new TextField();

        pencarian.setPromptText(
            "Cari barang atau kategori..."
        );

        pencarian.setPrefWidth(290);
        aturInput(pencarian);

        Button cari = buatTombol("CARI");
        cari.setPrefWidth(85);

        HBox searchBox = new HBox(9, pencarian, cari);

        searchBox.setAlignment(Pos.CENTER_RIGHT);

        Region ruangHeader = new Region();
        HBox.setHgrow(ruangHeader, Priority.ALWAYS);

        HBox header = new HBox(
            judul, ruangHeader, searchBox
        );

        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(26, 28, 18, 28));

        // STATISTIK
        totalBarang = new Label("0");
        barangTersedia = new Label("0");
        barangTerjual = new Label("0");

        HBox statistik = new HBox(
            16,
            buatStatistik(
                "TOTAL BARANG", totalBarang, "Seluruh barang"
            ),
            buatStatistik(
                "TERSEDIA", barangTersedia, "Siap dibeli"
            ),
            buatStatistik(
                "TERJUAL", barangTerjual, "Sudah terjual"
            )
        );

        // KATALOG
        Label judulProduk = buatJudul(
            "Barang Terbaru", 21
        );

        Label deskripsiProduk = new Label(
            "Koleksi preloved dari seluruh pengguna ReWear."
        );

        deskripsiProduk.setTextFill(Color.web(MUTED));

        VBox infoProduk = new VBox(
            5, judulProduk, deskripsiProduk
        );

        areaProduk = new FlowPane();

        areaProduk.setHgap(17);
        areaProduk.setVgap(17);
        areaProduk.setPadding(new Insets(5, 2, 20, 2));

        ScrollPane scroll = new ScrollPane(areaProduk);

        scroll.setFitToWidth(true);

        scroll.setHbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        scroll.setStyle(
            "-fx-background: " + BACKGROUND + ";" +
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-border-color: transparent;"
        );

        VBox konten = new VBox(
            22,
            header,
            statistik,
            infoProduk,
            scroll
        );

        konten.setPadding(
            new Insets(0, 28, 20, 28)
        );

        konten.setStyle(
            "-fx-background-color: " + BACKGROUND + ";"
        );

        VBox.setVgrow(scroll, Priority.ALWAYS);
        HBox.setHgrow(konten, Priority.ALWAYS);

        BorderPane layout = new BorderPane();

        layout.setLeft(sidebar);
        layout.setCenter(konten);

        // EVENT SIDEBAR
        beranda.setOnAction(e -> {
            pencarian.clear();
            tampilkanProduk(semuaBarang);
        });

        jelajahi.setOnAction(e ->
            pencarian.requestFocus()
        );

        cari.setOnAction(e -> cariBarang());

        pencarian.setOnAction(e -> cariBarang());

        jual.setOnAction(e -> tampilkanFormJual());

        barangSaya.setOnAction(e -> tampilkanBarangSaya());

        profil.setOnAction(e ->
            tampilkanAlert(
                Alert.AlertType.INFORMATION,
                "Profil Pengguna",
                "Nama: " + user.getNama()
                    + "\nUsername: " + user.getUsername()
                    + "\nRole: " + user.getRole()
            )
        );

        logout.setOnAction(e -> tampilkanLogin());

        muatDataDashboard();

        stage.setScene(new Scene(layout, 1250, 780));
    }

    // =================================
    // FORM JUAL BARANG
    // =================================
    private void tampilkanFormJual() {

        Label judul = buatJudul("JUAL BARANG", 25);
        Label deskripsi = new Label("Tambahkan barang preloved milikmu ke ReWear.");
        deskripsi.setTextFill(Color.web(MUTED));

        TextField nama = new TextField();
        nama.setPromptText("Contoh: Hoodie Uniqlo");
        aturInput(nama);

        ComboBox<String> kategori = new ComboBox<>(
            javafx.collections.FXCollections.observableArrayList(
                "Pakaian", "Sepatu", "Tas", "Aksesoris", "Lainnya"
            )
        );
        kategori.setPromptText("Pilih kategori");
        kategori.setMaxWidth(Double.MAX_VALUE);
        kategori.setPrefHeight(43);

        TextField harga = new TextField();
        harga.setPromptText("Contoh: 75000");
        aturInput(harga);

        ComboBox<String> kondisi = new ComboBox<>(
            javafx.collections.FXCollections.observableArrayList(
                "Baru", "Seperti Baru", "Sangat Baik", "Baik", "Cukup Baik"
            )
        );
        kondisi.setPromptText("Pilih kondisi");
        kondisi.setMaxWidth(Double.MAX_VALUE);
        kondisi.setPrefHeight(43);

        TextField ukuran = new TextField();
        ukuran.setPromptText("Contoh: M / 39 / All Size");
        aturInput(ukuran);

        TextField merek = new TextField();
        merek.setPromptText("Masukkan merek barang");
        aturInput(merek);

        Label fotoInfo = new Label("Belum ada foto yang dipilih");
        fotoInfo.setTextFill(Color.web(MUTED));
        ImageView preview = new ImageView();
        preview.setFitWidth(230);
        preview.setFitHeight(170);
        preview.setPreserveRatio(true);
        preview.setSmooth(true);
        StackPane areaPreview = new StackPane(preview);
        areaPreview.setPrefHeight(180);
        areaPreview.setMaxWidth(Double.MAX_VALUE);
        areaPreview.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 10; -fx-border-color: " + BORDER + "; -fx-border-radius: 10;");
        Button pilihFoto = new Button("PILIH FOTO BARANG");
        File[] fotoTerpilih = new File[1];

        pilihFoto.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Pilih Foto Barang");
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("File Gambar", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = chooser.showOpenDialog(stage);
            if (file != null) {
                fotoTerpilih[0] = file;
                preview.setImage(new Image(file.toURI().toString(), 230, 170, true, true));
                fotoInfo.setText(file.getName());
            }
        });

        Button simpan = buatTombol("PUBLIKASIKAN BARANG");
        Button kembali = new Button("Kembali ke Beranda");

        VBox form = new VBox(
            12, judul, deskripsi,
            buatLabel("FOTO BARANG"), areaPreview, fotoInfo, pilihFoto,
            buatLabel("NAMA BARANG"), nama,
            buatLabel("KATEGORI"), kategori,
            buatLabel("HARGA (RP)"), harga,
            buatLabel("KONDISI BARANG"), kondisi,
            buatLabel("UKURAN"), ukuran,
            buatLabel("MEREK"), merek,
            simpan, kembali
        );
        form.setPadding(new Insets(28));
        form.setMaxWidth(540);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: " + BORDER + "; -fx-border-radius: 12;");

        ScrollPane scrollForm = new ScrollPane(form);
        scrollForm.setFitToWidth(true);
        scrollForm.setStyle("-fx-background: " + BACKGROUND + "; -fx-background-color: " + BACKGROUND + "; -fx-border-color: transparent;");
        VBox halaman = new VBox(scrollForm);
        halaman.setAlignment(Pos.CENTER);
        halaman.setPadding(new Insets(25));
        halaman.setStyle("-fx-background-color: " + BACKGROUND + ";");

        simpan.setOnAction(e -> {
            String namaBarang = nama.getText().trim();
            String hargaBarang = harga.getText().trim();
            if (namaBarang.isEmpty() || kategori.getValue() == null || hargaBarang.isEmpty() || kondisi.getValue() == null) {
                tampilkanAlert(Alert.AlertType.WARNING, "Data Belum Lengkap", "Nama, kategori, harga, dan kondisi wajib diisi.");
                return;
            }
            if (fotoTerpilih[0] == null) {
                tampilkanAlert(Alert.AlertType.WARNING, "Foto Belum Dipilih", "Pilih foto barang terlebih dahulu agar pembeli dapat melihat barangnya.");
                return;
            }
            try {
                double nilaiHarga = Double.parseDouble(hargaBarang);
                if (nilaiHarga <= 0) {
                    tampilkanAlert(Alert.AlertType.WARNING, "Harga Tidak Valid", "Harga harus lebih dari nol.");
                    return;
                }
                Path folderFoto = Path.of("foto_barang");
                Files.createDirectories(folderFoto);
                String namaFile = UUID.randomUUID().toString() + "_" + fotoTerpilih[0].getName().replaceAll("[^A-Za-z0-9._-]", "_");
                Path tujuanFoto = folderFoto.resolve(namaFile);
                Files.copy(fotoTerpilih[0].toPath(), tujuanFoto, StandardCopyOption.REPLACE_EXISTING);

                boolean berhasil = barangDAO.tambahBarang(
                    userAktif.getIdUser(), namaBarang, kategori.getValue(), nilaiHarga,
                    kondisi.getValue(), ukuran.getText().trim(), merek.getText().trim(),
                    tujuanFoto.toAbsolutePath().toString()
                );
                if (berhasil) {
                    tampilkanAlert(Alert.AlertType.INFORMATION, "Berhasil!", "Barang dan foto berhasil dipublikasikan.");
                    tampilkanDashboard(userAktif);
                }
            } catch (NumberFormatException ex) {
                tampilkanAlert(Alert.AlertType.WARNING, "Harga Tidak Valid", "Masukkan harga berupa angka.");
            } catch (Exception ex) {
                tampilkanAlert(Alert.AlertType.ERROR, "Gagal Menambahkan Barang", ex.getMessage());
            }
        });
        kembali.setOnAction(e -> tampilkanDashboard(userAktif));
        stage.setScene(new Scene(halaman, 1150, 780));
    }

    // =================================
    // HALAMAN BARANG SAYA
    // =================================
    private void tampilkanBarangSaya() {

        Label judul = buatJudul("BARANG SAYA", 25);
        Label deskripsi = new Label(
            "Daftar barang yang sudah kamu publikasikan di ReWear."
        );
        deskripsi.setTextFill(Color.web(MUTED));

        Button kembali = buatTombol("KEMBALI KE BERANDA");
        kembali.setMaxWidth(220);
        kembali.setOnAction(e -> tampilkanDashboard(userAktif));

        FlowPane daftarKartu = new FlowPane();
        daftarKartu.setHgap(17);
        daftarKartu.setVgap(17);
        daftarKartu.setPadding(new Insets(10, 0, 20, 0));

        try {
            List<BarangItem> barangSaya =
                barangDAO.getBarangMilikUser(userAktif.getIdUser());

            if (barangSaya.isEmpty()) {
                Label kosong = new Label(
                    "Kamu belum memiliki barang yang dipublikasikan."
                );
                kosong.setFont(Font.font("Arial", FontWeight.BOLD, 14));
                kosong.setTextFill(Color.web(MUTED));
                kosong.setPadding(new Insets(20));
                daftarKartu.getChildren().add(kosong);
            } else {
                for (BarangItem barang : barangSaya) {
                    VBox kartu = new VBox(9);
                    StackPane fotoBarang = buatTampilanFoto(barang.getFoto());
                    fotoBarang.setPrefHeight(150);
                    Label nama = buatJudul(barang.getNama(), 16);
                    Label kategori = new Label(
                        "Kategori: " + barang.getKategori()
                    );
                    Label kondisi = new Label(
                        "Kondisi: " + barang.getKondisi()
                    );
                    Label ukuran = new Label(
                        "Ukuran: " + (barang.getUkuran() == null
                            || barang.getUkuran().isBlank()
                            ? "-" : barang.getUkuran())
                    );
                    Label merek = new Label(
                        "Merek: " + (barang.getMerek() == null
                            || barang.getMerek().isBlank()
                            ? "-" : barang.getMerek())
                    );
                    Label harga = new Label(
                        rupiah.format(barang.getHarga())
                    );
                    harga.setFont(Font.font("Arial", FontWeight.BOLD, 17));
                    harga.setTextFill(Color.web(DARK_BLUE));

                    Label status = new Label(
                        "Status: " + barang.getStatus()
                    );
                    status.setTextFill(Color.web(
                        "TERSEDIA".equalsIgnoreCase(barang.getStatus())
                            ? "#16804A" : "#A33B3B"
                    ));

                    Button edit = new Button("EDIT BARANG");
                    Button hapus = new Button("HAPUS BARANG");
                    Button statusButton = new Button(
                        "TERSEDIA".equalsIgnoreCase(barang.getStatus()) ? "TANDAI TERJUAL" : "TANDAI TERSEDIA"
                    );
                    edit.setMaxWidth(Double.MAX_VALUE);
                    hapus.setMaxWidth(Double.MAX_VALUE);
                    statusButton.setMaxWidth(Double.MAX_VALUE);

                    edit.setOnAction(e -> editBarangDialog(barang));
                    hapus.setOnAction(e -> {
                        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION);
                        konfirmasi.setTitle("Hapus Barang");
                        konfirmasi.setHeaderText("Hapus \"" + barang.getNama() + "\"?");
                        konfirmasi.setContentText("Barang yang dihapus tidak dapat dikembalikan.");
                        if (konfirmasi.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                            try {
                                if (barangDAO.hapusBarang(barang.getIdBarang(), userAktif.getIdUser())) {
                                    tampilkanAlert(Alert.AlertType.INFORMATION, "Berhasil", "Barang berhasil dihapus.");
                                    tampilkanBarangSaya();
                                }
                            } catch (Exception ex) {
                                tampilkanAlert(Alert.AlertType.ERROR, "Gagal Menghapus", ex.getMessage());
                            }
                        }
                    });
                    statusButton.setOnAction(e -> {
                        String statusBaru = "TERSEDIA".equalsIgnoreCase(barang.getStatus()) ? "TERJUAL" : "TERSEDIA";
                        try {
                            barangDAO.ubahStatusBarang(barang.getIdBarang(), userAktif.getIdUser(), statusBaru);
                            tampilkanBarangSaya();
                        } catch (Exception ex) {
                            tampilkanAlert(Alert.AlertType.ERROR, "Gagal Mengubah Status", ex.getMessage());
                        }
                    });

                    kartu.getChildren().addAll(
                        fotoBarang, nama, kategori, kondisi, ukuran, merek, harga, status,
                        edit, statusButton, hapus
                    );
                    kartu.setPadding(new Insets(16));
                    kartu.setPrefWidth(260);
                    kartu.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + BORDER + ";" +
                        "-fx-border-radius: 12;"
                    );
                    daftarKartu.getChildren().add(kartu);
                }
            }
        } catch (Exception ex) {
            tampilkanAlert(
                Alert.AlertType.ERROR,
                "Gagal Memuat Barang",
                ex.getMessage()
            );
        }

        ScrollPane scroll = new ScrollPane(daftarKartu);
        scroll.setFitToWidth(true);
        scroll.setStyle(
            "-fx-background: " + BACKGROUND + ";" +
            "-fx-background-color: " + BACKGROUND + ";" +
            "-fx-border-color: transparent;"
        );

        VBox halaman = new VBox(18, judul, deskripsi, kembali, scroll);
        halaman.setPadding(new Insets(30));
        halaman.setStyle("-fx-background-color: " + BACKGROUND + ";");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        stage.setScene(new Scene(halaman, 1150, 780));
    }

    // FORM EDIT BARANG
    private void editBarangDialog(BarangItem barang) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Barang");
        dialog.setHeaderText("Ubah informasi barang");

        TextField nama = new TextField(barang.getNama());
        TextField kategori = new TextField(barang.getKategori());
        TextField harga = new TextField(String.valueOf(barang.getHarga()));
        TextField kondisi = new TextField(barang.getKondisi());
        TextField ukuran = new TextField(barang.getUkuran() == null ? "" : barang.getUkuran());
        TextField merek = new TextField(barang.getMerek() == null ? "" : barang.getMerek());

        VBox isi = new VBox(8, new Label("Nama barang"), nama, new Label("Kategori"), kategori,
                new Label("Harga (Rp)"), harga, new Label("Kondisi"), kondisi,
                new Label("Ukuran"), ukuran, new Label("Merek"), merek);
        isi.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(isi);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(tombol -> {
            if (tombol == ButtonType.OK) {
                try {
                    if (nama.getText().isBlank() || kategori.getText().isBlank()
                            || kondisi.getText().isBlank() || harga.getText().isBlank()) {
                        tampilkanAlert(Alert.AlertType.WARNING, "Data Belum Lengkap", "Nama, kategori, harga, dan kondisi wajib diisi.");
                        return;
                    }
                    double nilaiHarga = Double.parseDouble(harga.getText().trim());
                    if (nilaiHarga <= 0) throw new NumberFormatException();
                    boolean berhasil = barangDAO.updateBarang(barang.getIdBarang(), userAktif.getIdUser(),
                            nama.getText().trim(), kategori.getText().trim(), nilaiHarga, kondisi.getText().trim(),
                            ukuran.getText().trim(), merek.getText().trim(), barang.getFoto());
                    if (berhasil) {
                        tampilkanAlert(Alert.AlertType.INFORMATION, "Berhasil", "Data barang berhasil diperbarui.");
                        tampilkanBarangSaya();
                    }
                } catch (NumberFormatException ex) {
                    tampilkanAlert(Alert.AlertType.WARNING, "Harga Tidak Valid", "Harga harus berupa angka lebih dari nol.");
                } catch (Exception ex) {
                    tampilkanAlert(Alert.AlertType.ERROR, "Gagal Mengedit", ex.getMessage());
                }
            }
        });
    }

    // =================================
    // LOAD DATA MYSQL
    // =================================

    private void muatDataDashboard() {

        try {

            semuaBarang = barangDAO.getSemuaBarang();

            totalBarang.setText(
                String.valueOf(
                    barangDAO.hitungBarang(null)
                )
            );

            barangTersedia.setText(
                String.valueOf(
                    barangDAO.hitungBarang("TERSEDIA")
                )
            );

            barangTerjual.setText(
                String.valueOf(
                    barangDAO.hitungBarang("TERJUAL")
                )
            );

            tampilkanProduk(semuaBarang);

        } catch (Exception ex) {

            tampilkanAlert(
                Alert.AlertType.ERROR,
                "Gagal Memuat Barang",
                ex.getMessage()
            );
        }
    }

    // =================================
    // KARTU PRODUK
    // =================================

    private void tampilkanProduk(
        List<BarangItem> daftar
    ) {

        areaProduk.getChildren().clear();

        if (daftar.isEmpty()) {

            Label kosong = new Label(
                "Belum ada barang yang tersedia."
            );

            kosong.setFont(
                Font.font("Arial", FontWeight.BOLD, 14)
            );

            kosong.setTextFill(Color.web(MUTED));
            kosong.setPadding(new Insets(25));

            areaProduk.getChildren().add(kosong);

            return;
        }

        for (BarangItem barang : daftar) {

            VBox kartu = new VBox(10);

            StackPane gambar = buatTampilanFoto(barang.getFoto());
            gambar.setPrefHeight(145);
            gambar.setMaxWidth(Double.MAX_VALUE);

            Label kategori = new Label(
                barang.getKategori().toUpperCase()
            );

            kategori.setFont(
                Font.font("Arial", FontWeight.BOLD, 10)
            );

            kategori.setTextFill(Color.web(BLUE));

            Label nama = new Label(barang.getNama());

            nama.setFont(
                Font.font("Arial", FontWeight.BOLD, 15)
            );

            nama.setTextFill(Color.web(TEXT));
            nama.setWrapText(true);

            Label merek = new Label(
                barang.getMerek() == null
                    ? "Tanpa merek"
                    : barang.getMerek()
            );

            merek.setTextFill(Color.web(MUTED));

            Label harga = new Label(
                rupiah.format(barang.getHarga())
            );

            harga.setFont(
                Font.font("Arial", FontWeight.BOLD, 17)
            );

            harga.setTextFill(Color.web(DARK_BLUE));

            Label penjual = new Label(
                "Penjual: " + barang.getNamaPenjual()
            );

            penjual.setTextFill(Color.web(MUTED));
            penjual.setWrapText(true);

            Label status = new Label(
                barang.getStatus()
            );

            status.setFont(
                Font.font("Arial", FontWeight.BOLD, 11)
            );

            status.setTextFill(Color.web(
                "TERSEDIA".equalsIgnoreCase(
                    barang.getStatus()
                ) ? "#16804A" : "#A33B3B"
            ));

            Button detail = buatTombol("LIHAT DETAIL");
            detail.setOnAction(e -> tampilkanDetailBarang(barang));

            Button whatsapp = buatTombol("HUBUNGI PENJUAL");
            whatsapp.setOnAction(e -> hubungiPenjual(barang));
            if (!"TERSEDIA".equalsIgnoreCase(barang.getStatus())) {
                whatsapp.setDisable(true);
                whatsapp.setText("BARANG SUDAH TERJUAL");
            }

            kartu.getChildren().addAll(
                gambar, kategori, nama, merek, harga,
                penjual, status, detail, whatsapp
            );

            kartu.setPadding(new Insets(14));
            kartu.setPrefWidth(245);
            kartu.setMaxWidth(245);

            kartu.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 12;" +
                "-fx-border-color: " + BORDER + ";" +
                "-fx-border-radius: 12;"
            );

            areaProduk.getChildren().add(kartu);
        }
    }


    // =================================
    // DETAIL PRODUK DAN WHATSAPP
    // =================================
    private void tampilkanDetailBarang(BarangItem barang) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Detail Barang ReWear");
        dialog.setHeaderText("Informasi Produk");

        StackPane foto = buatTampilanFoto(barang.getFoto());
        foto.setPrefHeight(200);
        Label nama = buatJudul(barang.getNama(), 22);
        Label harga = new Label(rupiah.format(barang.getHarga()));
        harga.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        harga.setTextFill(Color.web(BLUE));

        Label kategori = new Label("Kategori: " + barang.getKategori());
        Label kondisi = new Label("Kondisi: " + barang.getKondisi());
        Label ukuran = new Label("Ukuran: " + (barang.getUkuran() == null ? "-" : barang.getUkuran()));
        Label merek = new Label("Merek: " + (barang.getMerek() == null ? "-" : barang.getMerek()));
        Label penjual = new Label("Penjual: " + barang.getNamaPenjual());
        Label status = new Label("Status: " + barang.getStatus());

        VBox isi = new VBox(12, foto, nama, harga, kategori, kondisi, ukuran, merek, penjual, status);
        isi.setPadding(new Insets(15));
        isi.setPrefWidth(360);
        dialog.getDialogPane().setContent(isi);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void hubungiPenjual(BarangItem barang) {
        try {
            String nomor = barang.getWhatsappPenjual();
            if (nomor == null || nomor.isBlank()) {
                tampilkanAlert(Alert.AlertType.WARNING, "Nomor Tidak Tersedia",
                        "Penjual belum mencantumkan nomor WhatsApp.");
                return;
            }
            nomor = nomor.replaceAll("[^0-9]", "");
            if (nomor.startsWith("0")) nomor = "62" + nomor.substring(1);
            else if (nomor.startsWith("8")) nomor = "62" + nomor;

            String pesan = "Halo, saya tertarik dengan barang " + barang.getNama()
                    + " yang dijual di ReWear. Apakah masih tersedia?";
            String url = "https://wa.me/" + nomor + "?text="
                    + URLEncoder.encode(pesan, StandardCharsets.UTF_8);
            getHostServices().showDocument(url);
        } catch (Exception ex) {
            tampilkanAlert(Alert.AlertType.ERROR, "Gagal Membuka WhatsApp", ex.getMessage());
        }
    }

    private StackPane buatTampilanFoto(String lokasiFoto) {
        StackPane wadah = new StackPane();
        wadah.setMinHeight(135);
        wadah.setStyle("-fx-background-color: " + LIGHT_BLUE + "; -fx-background-radius: 9;");
        if (lokasiFoto != null && !lokasiFoto.isBlank()) {
            File file = new File(lokasiFoto);
            if (file.exists()) {
                ImageView imageView = new ImageView(new Image(file.toURI().toString(), 220, 145, true, true));
                imageView.setFitWidth(220);
                imageView.setFitHeight(145);
                imageView.setPreserveRatio(true);
                wadah.getChildren().add(imageView);
                return wadah;
            }
        }
        Label placeholder = new Label("FOTO BELUM TERSEDIA");
        placeholder.setTextFill(Color.web(DARK_BLUE));
        placeholder.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        wadah.getChildren().add(placeholder);
        return wadah;
    }

    // =================================
    // PENCARIAN
    // =================================

    private void cariBarang() {

        String kata = pencarian.getText()
            .trim().toLowerCase();

        List<BarangItem> hasil = new ArrayList<>();

        for (BarangItem barang : semuaBarang) {

            boolean cocok =
                barang.getNama().toLowerCase().contains(kata)
                || barang.getKategori().toLowerCase().contains(kata)
                || (barang.getMerek() != null
                    && barang.getMerek().toLowerCase()
                        .contains(kata));

            if (cocok) {
                hasil.add(barang);
            }
        }

        tampilkanProduk(hasil);
    }

    // =================================
    // KARTU STATISTIK
    // =================================

    private VBox buatStatistik(
        String judul,
        Label angka,
        String detail
    ) {

        Label nama = buatLabel(judul);

        angka.setFont(
            Font.font("Arial", FontWeight.BOLD, 28)
        );

        angka.setTextFill(Color.web(DARK_BLUE));

        Label keterangan = new Label(detail);
        keterangan.setTextFill(Color.web(MUTED));

        VBox kartu = new VBox(
            9, nama, angka, keterangan
        );

        kartu.setPadding(new Insets(19));
        kartu.setPrefWidth(210);

        kartu.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 12;"
        );

        return kartu;
    }

    // =================================
    // TOMBOL SIDEBAR
    // =================================

    private Button tombolMenu(
        String teks,
        boolean aktif
    ) {

        Button tombol = new Button(teks);

        tombol.setAlignment(Pos.CENTER_LEFT);
        tombol.setMaxWidth(Double.MAX_VALUE);
        tombol.setPrefHeight(45);

        tombol.setFont(
            Font.font("Arial", FontWeight.BOLD, 13)
        );

        tombol.setStyle(
            "-fx-background-color: "
            + (aktif ? BLUE : NAVY) + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        return tombol;
    }

    // =================================
    // JUDUL DAN LABEL
    // =================================

    private Label buatJudul(
        String teks,
        int ukuran
    ) {

        Label label = new Label(teks);

        label.setFont(
            Font.font("Arial", FontWeight.EXTRA_BOLD, ukuran)
        );

        label.setTextFill(Color.web(NAVY));

        return label;
    }

    private Label buatJudulPutih(
        String teks,
        int ukuran
    ) {

        Label label = new Label(teks);

        label.setFont(
            Font.font("Arial", FontWeight.EXTRA_BOLD, ukuran)
        );

        label.setTextFill(Color.WHITE);

        return label;
    }

    private Label buatLabel(String teks) {

        Label label = new Label(teks);

        label.setFont(
            Font.font("Arial", FontWeight.BOLD, 12)
        );

        label.setTextFill(Color.web(TEXT));

        return label;
    }

    // =================================
    // INPUT
    // =================================

    private void aturInput(TextField input) {

        input.setPrefHeight(43);

        input.setFont(
            Font.font("Arial", 13)
        );

        input.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + TEXT + ";" +
            "-fx-prompt-text-fill: #8293A6;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER + ";" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 9 12;"
        );
    }

    // =================================
    // TOMBOL
    // =================================

    private Button buatTombol(String teks) {

        Button tombol = new Button(teks);

        tombol.setMaxWidth(Double.MAX_VALUE);
        tombol.setPrefHeight(44);

        tombol.setFont(
            Font.font("Arial", FontWeight.BOLD, 13)
        );

        tombol.setStyle(
            "-fx-background-color: " + BLUE + ";" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 8;" +
            "-fx-cursor: hand;"
        );

        return tombol;
    }

    // =================================
    // ALERT
    // =================================

    private void tampilkanAlert(
        Alert.AlertType tipe,
        String judul,
        String pesan
    ) {

        Alert alert = new Alert(tipe);

        alert.setTitle("ReWear Notification");
        alert.setHeaderText(judul);
        alert.setContentText(pesan);

        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
