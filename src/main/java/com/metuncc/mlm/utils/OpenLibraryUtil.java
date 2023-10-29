package com.metuncc.mlm.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.dto.OpenLibraryBookAuthor;
import com.metuncc.mlm.dto.OpenLibraryBookAuthorDetail;
import com.metuncc.mlm.dto.OpenLibraryBookDetails;
import com.metuncc.mlm.external.ApiCall;

import java.util.Objects;

public class OpenLibraryUtil {
    private ApiCall apiCall;

    public OpenLibraryUtil(ApiCall apiCall) {
        this.apiCall = apiCall;
    }

    public OpenLibraryBookDetails getByISBN(String isbn){

        String endpoint = "https://openlibrary.org/books/"+isbn+".json";
        OpenLibraryBookDetails  dto = null;
        String body = apiCall.get(endpoint);
        if(Objects.nonNull(body)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                dto = objectMapper.readValue(body, OpenLibraryBookDetails.class);
                for (OpenLibraryBookAuthor author : dto.getAuthors()) {
                    if(Objects.nonNull(author.getKey())){
                        author.setKey(getByRef(author.getKey()));
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return dto;
    }


    public String getByRef(String ref){
        String endpoint = "https://openlibrary.org/authors/"+ref+".json";
        OpenLibraryBookAuthorDetail dto = null;
        String body = apiCall.get(endpoint);
        if(Objects.nonNull(body)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                dto = objectMapper.readValue(body, OpenLibraryBookAuthorDetail.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
        return dto.getName();
    }
}
