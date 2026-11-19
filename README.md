# ReWear – Marketplace Preloved

## Deskripsi
ReWear merupakan aplikasi marketplace preloved berbasis JavaFX yang dikembangkan menggunakan bahasa pemrograman Java dan database MySQL. Aplikasi ini dibuat untuk memudahkan pengguna dalam menjual dan mencari barang preloved secara online.

Melalui ReWear, pengguna dapat melihat katalog barang, menambahkan barang yang ingin dijual, serta mengelola barang miliknya sendiri. Aplikasi ini juga menyediakan fitur admin untuk membantu pengelolaan data pengguna dan barang.

## Fitur Aplikasi

### 1. Fitur Pengguna
- Registrasi akun baru.
- Login menggunakan username dan password.
- Melihat katalog barang preloved.
- Mencari barang berdasarkan nama.
- Melihat detail informasi barang.
- Menambahkan barang untuk dijual.
- Mengunggah foto barang.
- Melihat dan mengelola barang milik sendiri.
- Mengedit dan menghapus barang.
- Mengubah status barang menjadi tersedia atau terjual.
- Menghubungi penjual melalui WhatsApp.

### 2. Fitur Admin
- Login sebagai admin.
- Melihat dashboard admin.
- Melihat data pengguna.
- Melihat seluruh data barang.
- Menghapus data barang.

## Teknologi yang Digunakan
- **Java** – Bahasa pemrograman utama.
- **JavaFX** – Framework untuk membangun antarmuka grafis (GUI).
- **MySQL** – Database untuk menyimpan data aplikasi.
- **JDBC** – Menghubungkan aplikasi dengan database MySQL.
- **Maven** – Mengelola dependensi dan konfigurasi project.
- **NetBeans IDE** – Lingkungan pengembangan aplikasi.

## Struktur Project

ReWearGUI/
├── foto_barang/
├── src/
│   └── main/
│       └── java/
│           └── com/rewear/reweargui/
│               ├── database/
│               ├── model/
│               ├── ReWearGUI.java
│               └── AdminDashboard.java
├── pom.xml
└── nbactions.xml


## Database
Aplikasi ini menggunakan MySQL dengan nama database `rewear_db`.

Database digunakan untuk menyimpan:
- Data pengguna.
- Data barang.
- Informasi penjual.
- Status barang yang dijual.

## Cara Menjalankan Aplikasi

1. Clone atau download repository ReWearGUI.
2. Buka project menggunakan NetBeans IDE.
3. Pastikan Java dan Maven sudah terpasang.
4. Siapkan database MySQL dengan nama `rewear_db`.
5. Sesuaikan konfigurasi koneksi database pada file `DatabaseConnection.java`.
6. Pastikan tabel database sudah dibuat sesuai kebutuhan aplikasi.
7. Jalankan project melalui file `ReWearGUI.java`.

## Tujuan Pengembangan
Project ini dikembangkan sebagai implementasi mata kuliah Pemrograman Berorientasi Objek (PBO), khususnya dalam penerapan bahasa pemrograman Java, konsep pemrograman berorientasi objek, pengembangan GUI menggunakan JavaFX, serta integrasi database MySQL.

---

**ReWear – Give Your Preloved Items a New Life!** ♻️
