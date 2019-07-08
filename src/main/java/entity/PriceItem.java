package entity;

import java.math.BigDecimal;

/*
 * Класс описание сущности PriceItem
 * */
public class PriceItem {

    private Long id;
    private String vendor;
    private String itemNumber;
    private String searchVendor;
    private String searchItemNumber;
    private String description;
    private BigDecimal price;
    private int count;

    public PriceItem(String vendor, String itemNumber, String searchVendor,
                     String searchItemNumber, String description, BigDecimal price, int count) {
        this.vendor = vendor;
        this.itemNumber = itemNumber;
        this.searchVendor = searchVendor;
        this.searchItemNumber = searchItemNumber;
        this.description = description;
        this.price = price;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getItemNumber() {
        return itemNumber;
    }

    public void setItemNumber(String itemNumber) {
        this.itemNumber = itemNumber;
    }

    public String getSearchVendor() {
        return searchVendor;
    }

    public void setSearchVendor(String searchVendor) {
        this.searchVendor = searchVendor;
    }

    public String getSearchItemNumber() {
        return searchItemNumber;
    }

    public void setSearchItemNumber(String searchItemNumber) {
        this.searchItemNumber = searchItemNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
