
package com.rewear.reweargui.model;

public class User {

    private int idUser;
    private String nama;
    private String username;
    private String password;
    private String nomorWhatsapp;
    private String role;

    public User(int idUser, String nama, String username,
                String password, String nomorWhatsapp,
                String role) {

        this.idUser = idUser;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.nomorWhatsapp = nomorWhatsapp;
        this.role = role;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getNama() {
        return nama;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNomorWhatsapp() {
        return nomorWhatsapp;
    }

    public String getRole() {
        return role;
    }
}
