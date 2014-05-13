package com.oresomecraft.ism.db.Factory;

import com.oresomecraft.ism.db.Database;
import com.oresomecraft.ism.db.Delegates.FilenameDatabase;
import com.oresomecraft.ism.db.Delegates.FilenameDatabaseImpl;
import com.oresomecraft.ism.db.Delegates.HostnameDatabase;
import com.oresomecraft.ism.db.Delegates.HostnameDatabaseImpl;
import com.oresomecraft.ism.db.MySQL;

/**
 * Factory for Database objects.<br>
 * Date Created: 2012-03-11 15:07.
 *
 * @author Balor (aka Antoine Aflalo)
 */
public class DatabaseFactory {
    public static Database createDatabase(DatabaseConfig config) throws InvalidConfigurationException {
        if (!config.isValid())
            throw new InvalidConfigurationException(
                    "The configuration is invalid, you don't have enough parameters for that DB : "
                            + config.getType());
        switch (config.getType()) {
            case MySQL:
                return new MySQL(config.getLog(), config.getParameter(DatabaseConfig.Parameter.PREFIX),
                        config.getParameter(DatabaseConfig.Parameter.HOSTNAME),
                        Integer.parseInt(config.getParameter(DatabaseConfig.Parameter.PORTNMBR)),
                        config.getParameter(DatabaseConfig.Parameter.DATABASE),
                        config.getParameter(DatabaseConfig.Parameter.USERNAME),
                        config.getParameter(DatabaseConfig.Parameter.PASSWORD));
            default:
                return null;
        }
    }

    public static HostnameDatabase hostname() {
        return new HostnameDatabaseImpl();
    }

    public static FilenameDatabase filename() {
        return new FilenameDatabaseImpl();
    }
}
