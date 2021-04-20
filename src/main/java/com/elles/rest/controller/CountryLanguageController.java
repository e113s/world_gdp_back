package com.elles.rest.controller;

import com.elles.rest.dao.CountryLanguageDAO;
import com.elles.rest.model.CountryLanguage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/languages")
public class CountryLanguageController {

    @Autowired
    private CountryLanguageDAO countryLanguageDAO;

    private Logger log = LoggerFactory.getLogger(CountryLanguageController.class);

    @GetMapping("/{countryCode}")
    public ResponseEntity<?> getLanguages(@PathVariable String countryCode,
                                          @RequestParam(name="pageNo", defaultValue="1") Integer pageNo){
        try {
            return ResponseEntity.ok(countryLanguageDAO.getLanguages(countryCode, pageNo));
        }catch(Exception ex) {
            System.out.println("Error while getting languages for country: {}"+
                    countryCode+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while languages cities");
        }

    }

    @PostMapping("/{countryCode}")
    public ResponseEntity<?> addLanguage(@PathVariable String countryCode,
                                         @Valid @RequestBody CountryLanguage language){
        try {
            log.debug("REST request to add new Language: ", language);
            if ( countryLanguageDAO.languageExists(countryCode, language.getLanguage())) {
                return ResponseEntity.badRequest()
                        .body("Language already exists for country");
            }
            countryLanguageDAO.addLanguage(countryCode, language);
            return ResponseEntity.ok(language);
        }catch(Exception ex) {
            System.out.println("Error while adding language: {} to country: {}"+
                    language+ countryCode+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while adding language");
        }
    }

    @DeleteMapping("/{countryCode}/language/{language}")
    public ResponseEntity<?> deleteLanguage(@PathVariable String countryCode,
                                            @PathVariable String language){
        try {
            countryLanguageDAO.deleteLanguage(countryCode, language);;
            return ResponseEntity.ok().build();
        }catch(Exception ex) {
            System.out.println("Error occurred while deleting language : {}, for country: {}"+
                    language+ countryCode+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting the language");
        }
    }
}
