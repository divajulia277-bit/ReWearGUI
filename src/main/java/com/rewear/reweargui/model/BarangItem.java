package com.rewear.reweargui.model;

public class BarangItem {

    private int idBarang;
    private int idUser;
    private String nama;
    private String kategori;
    private double harga;
    private String kondisi;
    private String ukuran;
    private String merek;
    private String status;
    private String namaPenjual;
    private String foto;
    private String whatsappPenjual;

    public BarangItem(int idBarang, int idUser, String nama,
            String kategori, double harga, String kondisi,
            String ukuran, String merek, String status,
            String namaPenjual, String foto, String whatsappPenjual) {

        this.idBarang = idBarang;
        this.idUser = idUser;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
        this.kondisi = kondisi;
        this.ukuran = ukuran;
        this.merek = merek;
        this.status = status;
        this.namaPenjual = namaPenjual;
        this.foto = foto;
        this.whatsappPenjual = whatsappPenjual;
    }

    public int getIdBarang() { return idBarang; }
    public int getIdUser() { return idUser; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
    public double getHarga() { return harga; }
    public String getKondisi() { return kondisi; }
    public String getUkuran() { return ukuran; }
    public String getMerek() { return merek; }
    public String getStatus() { return status; }
    public String getNamaPenjual() { return namaPenjual; }
    public String getFoto() { return foto; }
    public String getWhatsappPenjual() { return whatsappPenjual; }
}