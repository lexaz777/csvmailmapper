package utils;


import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Preferences {
    private static Preferences ourInstance = new Preferences();
    private static final String PREFERENCES_FILENAME = "data.properties";
    private Properties properties;

    public static Preferences getInstance() {
        return ourInstance;
    }

    private Preferences() {
        properties = new Properties();
        try {
            properties.load(new FileReader(PREFERENCES_FILENAME));
        } catch (IOException e) {
            System.out.println("File " + PREFERENCES_FILENAME + "not found!");
            e.printStackTrace();
        }
    }

    public String getProtocol() {
        return properties.getProperty("protocol");
    }

    public String getPort() {
        return properties.getProperty("port");
    }

    public String getUserName() {
        return properties.getProperty("username");
    }

    public String getPassword() {
        return properties.getProperty("password");
    }

    public String getServerAddress() {
        return properties.getProperty("host");
    }

    public String getDbDriverName() {
        return properties.getProperty("db_driver_name");
    }

    public String getDbConnectionString() {
        return properties.getProperty("db_connection_string");
    }

    public String getDbLogin() {
        return properties.getProperty("db_login");
    }

    public String getDbPassword() {
        return properties.getProperty("db_password");
    }

    public String getRequiredSenderAddress() {
        return properties.getProperty("required_sender");
    }

    public String getAttachmentFileExtension() {
        return properties.getProperty("file_extension");
    }

    public int getBrandColumn() {
        return Integer.parseInt(properties.getProperty("brand_column"));
    }

    public int getCatalogueNumberColumn() {
        return Integer.parseInt(properties.getProperty("catalogue_number"));
    }

    public int getDescriptionColumn() {
        return Integer.parseInt(properties.getProperty("description_column"));
    }

    public int getPriceColumn() {
        return Integer.parseInt(properties.getProperty("price_column"));
    }

    public int getCountColumn() {
        return Integer.parseInt(properties.getProperty("count_column"));
    }

}
