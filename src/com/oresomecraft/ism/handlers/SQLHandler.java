package com.oresomecraft.ism.handlers;

import com.oresomecraft.ism.ISM;
import com.oresomecraft.ism.db.MySQL;
import com.oresomecraft.ism.object.iPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHandler {

    ISM plugin = ISM.getInstance();

    public static synchronized void createTables() {
        if (ISM.offlineMode) {
            System.out.println("[ISM] ISM is running in offline mode, table check + creation aborted!");
            return;
        }

        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        mysql.open();

        if (!mysql.isTable(ISM.storageDatabase + "_users")) {
            try {
                mysql.query("CREATE TABLE `" + ISM.storageDatabase + "_users` (" +
                        "`id` INT(10) UNSIGNED NULL AUTO_INCREMENT," +
                        "`name` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci'," +
                        "`title` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci'," +
                        "`gold` INT(10) UNSIGNED NULL," +
                        "`silver` INT(10) UNSIGNED NULL," +
                        "`bronze` INT(10) UNSIGNED NULL," +
                        "PRIMARY KEY (`id`))");
            } catch (SQLException e) {
                e.printStackTrace();  //Table creation failed.
            }
        }

        if (!mysql.isTable(ISM.storageDatabase + "_rounds")) {
            try {
                mysql.query("CREATE TABLE `" + ISM.storageDatabase + "_rounds` (" +
                        "`id` INT(10) UNSIGNED NULL AUTO_INCREMENT," +
                        "`map` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci'," +
                        "`winner` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci'," +
                        "`gamemode` VARCHAR(255) NULL DEFAULT NULL COLLATE 'utf8_general_ci'," +
                        "PRIMARY KEY (`id`))");
            } catch (SQLException e) {
                e.printStackTrace();  //Table creation failed.
            }
        }

        mysql.close();
    }

    public static synchronized void refreshStats(iPlayer p) throws SQLException {
        if (ISM.offlineMode) {
            System.out.println("[ISM] ISM is running in offline mode! Stats refresh aborted!");
            return;
        }

        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        mysql.open();

        ResultSet rs = mysql.query("SELECT * FROM " + ISM.storageDatabase + "_users");

        while (rs.next()) {
            if (rs.getString("name").equalsIgnoreCase(p.getName())) {
                p.gold = rs.getInt("gold");
                p.silver = rs.getInt("silver");
                p.bronze = rs.getInt("bronze");
                p.title = rs.getString("title");
            }
        }

        mysql.close();
    }

    /**
     * This is a dangerous method, it has the potential to erase stats.
     *
     * @param p
     * @throws SQLException
     */
    public static synchronized void pushStats(iPlayer p) throws SQLException {
        if (ISM.offlineMode) {
            System.out.println("[ISM] ISM is running in offline mode! Stats push aborted!");
            return;
        }

        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);

        if (p == null) {
            System.out.println("[ISM] ISM's push safety check picked up that who disconnected had a null attribute and the stats push was aborted!");
            return;
        }

        if (p.title == null || p.title == "null") return;

        mysql.open();

        mysql.query("UPDATE `" + ISM.storageDatabase + "`.`" + ISM.storageDatabase + "_users` SET `gold` = '" + p.gold + "' WHERE `name` = '" + p.getName() + "'");
        mysql.query("UPDATE `" + ISM.storageDatabase + "`.`" + ISM.storageDatabase + "_users` SET `silver` = '" + p.silver + "' WHERE `name` = '" + p.getName() + "'");
        mysql.query("UPDATE `" + ISM.storageDatabase + "`.`" + ISM.storageDatabase + "_users` SET `bronze` = '" + p.bronze + "' WHERE `name` = '" + p.getName() + "'");
        mysql.query("UPDATE `" + ISM.storageDatabase + "`.`" + ISM.storageDatabase + "_users` SET `title` = '" + p.title + "' WHERE `name` = '" + p.getName() + "'");

        mysql.close();
    }

    public static synchronized void createNewUser(String p) throws SQLException {
        if (ISM.offlineMode) {
            System.out.println("[ISM] ISM is running in offline mode! User creation aborted!");
            return;
        }

        if (userExists(p)) return;
        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        mysql.open();

        mysql.query("INSERT INTO `" + ISM.storageDatabase + "_users` (`id`, `name`, `title`, `gold`, `silver`, `bronze`) VALUES (NULL, '" + p + "' , 'New Player', '0', '0', '0')");
        mysql.close();
    }

    private static synchronized boolean userExists(String p) throws SQLException {
        if (ISM.offlineMode) {
            System.out.println("[ISM] ISM is running in offline mode! User check aborted!");
            return false;
        }

        MySQL mysql = new MySQL(ISM.logger,
                "[ISM-DB] ",
                ISM.storageHostname,
                ISM.storagePort,
                ISM.storageDatabase,
                ISM.storageUsername,
                ISM.storagePassword);
        mysql.open();

        ResultSet rs = mysql.query("SELECT * FROM  `" + ISM.storageDatabase + "_users` WHERE  `name` =  '" + p + "'");
        while (rs.next()) {
            if (rs.getString("name").equals(p)) {
                mysql.close();
                return true;
            }
        }

        mysql.close();

        return false;
    }

    public static synchronized void logRound(String map, String winner, String gamemode) {
        synchronized (ISM.Input) {
            ISM.Input.add("INSERT INTO `" + ISM.storageDatabase + "`.`" + ISM.storageDatabase + "_rounds` (`id`, `map`, `winner`, `gamemode`) VALUES (NULL, '" + map + "', '" + winner + "', '" + gamemode + "')");
        }
    }

}
