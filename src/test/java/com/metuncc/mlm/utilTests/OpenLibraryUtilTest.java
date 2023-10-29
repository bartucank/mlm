package com.metuncc.mlm.utilTests;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metuncc.mlm.external.ApiCall;
import com.metuncc.mlm.utils.OpenLibraryUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

@ExtendWith(MockitoExtension.class)
public class OpenLibraryUtilTest {
    @Mock
    private ApiCall apiCall;

    @InjectMocks
    private OpenLibraryUtil openLibraryUtil;

    @Test
    public void getByISBN_nullCase(){
        Assert.isNull(openLibraryUtil.getByISBN("1"));
    }
    @Test
    public void getByISBN_validCase(){
        String json = "{\"other_titles\": [\"Bir dava, iki devrimci :\"], \"publishers\": [\"G\\u00fcz Yay\\u0131nlar\\u0131\"], \"subtitle\": \"Che Guevara, Deniz Gezmi\\u015f\", \"key\": \"/books/OL31076087M\", \"authors\": [{\"key\": \"/authors/OL8811965A\"}], \"publish_places\": [\"Zeytinburnu, \\u0130stanbul\"], \"subject_times\": [\"1960-1980\"], \"pagination\": \"376, [24] p.\", \"source_records\": [\"marc:marc_loc_2016/BooksAll.2016.part39.utf8:277527791:1165\", \"marc:marc_columbia/Columbia-extract-20221130-019.mrc:129104366:1146\"], \"title\": \"Bir dava, iki devrimci\", \"notes\": \"Includes bibliographical references (p. [377]).\", \"number_of_pages\": 376, \"languages\": [{\"key\": \"/languages/tur\"}], \"subject_places\": [\"Turkey\", \"Latin America\"], \"subjects\": [\"Socialism\", \"Communism\", \"Student movements\", \"Politics and government\", \"Guerrillas\", \"Revolutionaries\", \"T\\u00fcrkiye Halk Kurtulu\\u015f Ordusu\"], \"subject_people\": [\"Che Guevara (1928-1967)\", \"Deniz Gezmi\\u015f (1947-1972)\"], \"publish_country\": \"tu\", \"publish_date\": \"2011\", \"by_statement\": \"H\\u00fcseyin Turhan\", \"works\": [{\"key\": \"/works/OL23239569W\"}], \"type\": {\"key\": \"/type/edition\"}, \"identifiers\": {}, \"isbn_10\": [\"6053921939\"], \"isbn_13\": [\"9786053921936\"], \"lccn\": [\"2012347590\"], \"oclc_numbers\": [\"775410417\"], \"classifications\": {}, \"lc_classifications\": [\"HX376.7.A6 T84 2011\", \"DR593 .T873 2011\"], \"latest_revision\": 3, \"revision\": 3, \"created\": {\"type\": \"/type/datetime\", \"value\": \"2020-11-13T10:31:12.334491\"}, \"last_modified\": {\"type\": \"/type/datetime\", \"value\": \"2022-12-22T20:07:29.107249\"}}";
        when(apiCall.get(any())).thenReturn(json);
        Assert.notNull(openLibraryUtil.getByISBN("1"));
    }
    @Test
    public void getByISBN_exceptionCase() throws Exception {
        String json = "1";
        when(apiCall.get(any())).thenReturn(json);
        Assert.isNull(openLibraryUtil.getByISBN("1"));
    }
    @Test
    public void getByRef_exceptionCase() throws Exception {
        String json = "1";
        when(apiCall.get(any())).thenReturn(json);
        Assert.isNull(openLibraryUtil.getByRef("1"));
    }

}
