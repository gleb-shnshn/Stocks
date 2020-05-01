package shanshin.gleb.diplom.model;

public class UniversalStock {
    public String nameField, countField, priceField, priceEndField, deltaField, iconUrl;
    public boolean redOrGreen;
    public int id;

    public boolean equals(UniversalStock uStock) {
        return this.priceField.equals(uStock.priceField) && this.priceEndField.equals(uStock.priceEndField);
    }
}
