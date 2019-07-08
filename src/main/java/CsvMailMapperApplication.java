
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import datamapper.PriceItemMapper;
import entity.PriceItem;
import utils.MailReceiver;

import sql.DbConnection;
import utils.Preferences;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.*;

public class CsvMailMapperApplication {
    private PriceItemMapper priceItemMapper;
    private MailReceiver mailReceiver;
    private DbConnection dbConnection;
    private Preferences preferences;

    public CsvMailMapperApplication() {
        preferences = Preferences.getInstance();
        mailReceiver = new MailReceiver();
        priceItemMapper = new PriceItemMapper(getDbConnection());
    }

    public static void main(String[] args) {
        CsvMailMapperApplication csvMailMapperApplication = new CsvMailMapperApplication();
        csvMailMapperApplication.start();
    }

    private void start() {
        String mailProtocol = preferences.getProtocol();
        String mailHost = preferences.getServerAddress();
        String mailPort = preferences.getPort();

        String mailUsername = preferences.getUserName();
        String requiredSenderAddress = preferences.getRequiredSenderAddress();
        String mailPassword = preferences.getPassword();

        List<byte[]> receivedCsvFiles = mailReceiver.getMessages(mailProtocol, mailHost, mailPort,
                mailUsername, mailPassword, requiredSenderAddress);

        if (receivedCsvFiles.size() == 0) {
            System.out.println("Подходящих писем нет, парсить нечего");
        } else {
            System.out.println("Количество файлов: " + receivedCsvFiles.size());
            for (byte[] attachment : receivedCsvFiles) {
                priceItemMapper.disableAutoCommit();
                System.out.println("Начали обработку файлов");
                processAttachment(attachment);
                priceItemMapper.doCommit();
                System.out.println("Завершили обработку файлов");
                priceItemMapper.enableAutoCommit();
            }
        }
        closeDbConnection();
    }

    private void processAttachment(byte[] attachment) {
        int brandColumn = preferences.getBrandColumn();
        int catalogueNumberColumn = preferences.getCatalogueNumberColumn();
        int descriptionColumn = preferences.getDescriptionColumn();
        int priceColumn = preferences.getPriceColumn();
        int countColumn = preferences.getCountColumn();

        CsvParserSettings settings = getParserSettings();
        CsvParser parser = new CsvParser(settings);

        Reader in = new InputStreamReader(new ByteArrayInputStream(attachment));
        List<String[]> allRows = parser.parseAll(in);

        for (String[] line : allRows) {
            PriceItem priceItem = getPriceItem(line, brandColumn, catalogueNumberColumn,
                    descriptionColumn, priceColumn, countColumn);
            priceItemMapper.put(priceItem);
        }
        System.out.println("Обработано строк:" + allRows.size());
    }

    private Connection getDbConnection() {
        String dbDriverName = preferences.getDbDriverName();
        String dbConnectionString = preferences.getDbConnectionString();
        String dbLogin = preferences.getDbLogin();
        String dbPassword = preferences.getDbPassword();
        dbConnection = new DbConnection(dbDriverName, dbConnectionString, dbLogin, dbPassword);
        dbConnection.connect();
        return dbConnection.getConnection();
    }

    private void closeDbConnection() {
        dbConnection.close();
    }

    private CsvParserSettings getParserSettings() {
        CsvParserSettings settings = new CsvParserSettings();
        settings.getFormat().setLineSeparator("\r\n");
        settings.getFormat().setDelimiter(';');
        settings.getFormat().setQuote('"');
        settings.getFormat().setQuoteEscape('\0');
        settings.setMaxCharsPerColumn(16384);
        settings.setKeepEscapeSequences(true);
        settings.setMaxColumns(15);
        settings.setHeaderExtractionEnabled(true);
        return settings;
    }

    private PriceItem getPriceItem(String[] line, int brandColumn, int catalogueNumberColumn,
                                   int descriptionColumn, int priceColumn, int countColumn) {
        String brand = line[brandColumn];
        String catalogueNumber = line[catalogueNumberColumn];
        String rawDescription = line[descriptionColumn];
        String rawPrice = line[priceColumn];
        String rawCount = line[countColumn];

        String normalizedDescription = getNormalizedDescription(rawDescription);
        BigDecimal price = getBigDecimal(rawPrice);
        int count = getCount(rawCount);

        String searchVendor = getSearchVendor(brand);
        String searchNumber = getSearchNumber(catalogueNumber);

        return new PriceItem(brand, catalogueNumber, searchVendor, searchNumber,
                normalizedDescription, price, count);
    }

    private String getNormalizedDescription(String rawDescription) {
        String normalizedDescription;
        if (rawDescription.length() > 512)
            normalizedDescription = rawDescription.substring(0, 511);
        else normalizedDescription = rawDescription;
        return normalizedDescription;
    }

    private BigDecimal getBigDecimal(String rawPrice) {
        BigDecimal price;
        rawPrice = rawPrice.replace(",", ".");
        try {
            price = new BigDecimal(rawPrice);
        } catch (NumberFormatException ex) {
            System.out.println("Incorrect priceToParse = " + rawPrice);
            price = BigDecimal.valueOf(0);
        }
        return price;
    }

    private int getCount(String rawCount) {
        int count;
        if (rawCount.contains("-")) {
            rawCount = rawCount.replace(" ", "");
            String[] splittedPrice = rawCount.split("-");
            count = Integer.parseInt(splittedPrice[1]);
        } else if (rawCount.contains(">") || rawCount.contains("<")) {
            rawCount = rawCount.replace(">", "")
                    .replace("<", "").replace(" ", "");
            count = Integer.parseInt(rawCount);
        } else count = Integer.parseInt(rawCount.replace(" ", ""));
        return count;
    }

    private String getSearchVendor(String brand) {
        return brand.replaceAll("[^A-Za-zА-Яа-я0-9]", "")
                .toUpperCase();
    }

    private String getSearchNumber(String catalogueNumber) {
        return catalogueNumber.replaceAll("[^A-Za-zА-Яа-я0-9]", "")
                .toUpperCase();
    }
}
