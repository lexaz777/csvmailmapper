package datamapper;

import entity.PriceItem;
import sql.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/*
 * Класс реализует паттерн DataMapper для удобства внесения объектов в таблицу БД.
 * Методы get(), update(), delete() необходимо реализовать после определения стретегии
 * замены/обновления существующих записей в БД
 * */
public class PriceItemMapper implements DataMapper<PriceItem> {
    private Connection dbConnection;
    private String insertStatement = "INSERT INTO `datamapper`.`priceitems`  (`vendor`, `number`,`searchVendor`, `searchNumber`," +
            "`description`,`price`,`count`) VALUES (?,?,?,?,?,?,?);";

    public PriceItemMapper(Connection connection) {
        this.dbConnection = connection;
    }

    @Override
    public void put(PriceItem priceItem) {
        try {
            PreparedStatement ps = dbConnection.prepareStatement(insertStatement);
            ps.setString(1, priceItem.getVendor());
            ps.setString(2, priceItem.getItemNumber());
            ps.setString(3, priceItem.getSearchVendor());
            ps.setString(4, priceItem.getSearchItemNumber());
            ps.setString(5, priceItem.getDescription());
            ps.setBigDecimal(6, priceItem.getPrice());
            ps.setInt(7, priceItem.getCount());
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PriceItem get(int id) {
        //todo реализовать  метод после определения стратегии внесения совпадающих записей
        return null;
    }

    @Override
    public void update(PriceItem object) {
        //todo реализовать  метод после определения стратегии внесения совпадающих записей
    }

    @Override
    public void delete(PriceItem object) {
        //todo реализовать  метод после определения стратегии внесения совпадающих записей
    }

    public void doCommit() {
        try {
            dbConnection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disableAutoCommit() {
        try {
            dbConnection.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void enableAutoCommit() {
        try {
            dbConnection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
