package com.bugracdnc.restmvc.models;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BeerCSVRecord {

    @CsvBindByName
    private Integer row;

    @CsvBindByName(column = "count.x")
    private Integer count;

    @CsvBindByName
    private String abv;

    @CsvBindByName
    private String ibu;

    @CsvBindByName
    private Integer id;

    @CsvBindByName
    private String beer;

    @CsvBindByName
    private String style;

    @CsvBindByName(column = "brewery_id")
    private Integer breweryId;

    @CsvBindByName
    private Float ounces;

    @CsvBindByName
    private String style2;

    @CsvBindByName(column = "count.y")
    private Integer count_y;

    @CsvBindByName
    private String brewery;

    @CsvBindByName
    private String city;

    @CsvBindByName
    private String state;

    @CsvBindByName
    private String label;

    public BeerStyle getBeerStyle() {
        return switch(this.getStyle()) {
            case "American Pale Lager" -> BeerStyle.LAGER;
            case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                    BeerStyle.ALE;
            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
            case "American Porter" -> BeerStyle.PORTER;
            case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
            case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
            case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
            case "English Pale Ale" -> BeerStyle.PALE_ALE;
            default -> BeerStyle.PILSNER;
        };
    }
}
