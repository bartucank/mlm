package com.metuncc.mlm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDetails {
    private String title;
    private String full_title;
    private String subtitle;
    private String notes;
    private String[] publishers;
    private String publish_date;
    private String[] isbn_10;
    private String[] isbn_13;
    private String[] subjects;
    private OpenLibraryBookAuthor[] authors;

    private String img;
    private String imgName;
}
