
package com.rewear.reweargui.database;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabase {

    public static void main(String[] args) {

        try {
            Connection connection =
                    DatabaseConnection.getConnection();

            System.out.println(
                    "Koneksi database ReWear berhasil!"
            );

            connection.close();

        } catch (SQLException e) {

            System.out.println(
                    "Koneksi database gagal!"
            );

            e.printStackTrace();
        }
    }
}
