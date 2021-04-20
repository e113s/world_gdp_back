package com.elles.rest.controller;

import com.elles.rest.dao.CountryDAO;
import com.elles.rest.dao.LookupDAO;
import com.elles.rest.external.WorldBankAPIClient;
import com.elles.rest.model.Country;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    @Autowired
    private CountryDAO countryDao;

    @Autowired
    private LookupDAO lookupDAO;

    @Autowired
    private WorldBankAPIClient worldBankApiClient;

    private Logger log = LoggerFactory.getLogger(CountryController.class);

    @GetMapping
    public ResponseEntity<?> getCountries(
            @RequestParam(name="search", required = false) String searchTerm,
            @RequestParam(name="continent", required = false) String continent,
            @RequestParam(name="region", required = false) String region,
            @RequestParam(name="code", required = false) String code,
            @RequestParam(name="pageNo", required = false) Integer pageNo
    ){
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("search", searchTerm);
            params.put("continent", continent);
            params.put("region", region);
            params.put("code", code);
            if ( pageNo != null ) {
                params.put("pageNo", pageNo.toString());
            }

            List<Country> countries = countryDao.getCountries(params);
            Map<String, Object> response = new HashMap<String, Object>();
            response.put("list", countries);
            response.put("count", countryDao.getCountriesCount(params));
            return ResponseEntity.ok(response);
        }catch(Exception ex) {
            System.out.println("Error while getting countries"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting countries");
        }
    }

    @GetMapping("/{countryCode}/gdp")
    public ResponseEntity<?> getGDP(@PathVariable String countryCode){
        try {
            return ResponseEntity.ok(worldBankApiClient.getGDP(countryCode));
        }catch(Exception ex) {
            System.out.println("Error while getting GDP for country: {}"+ countryCode+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting the GDP");
        }
    }

    @PostMapping(value = "/{countryCode}",
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> editCountry(
            @PathVariable String countryCode, @Valid @RequestBody Country country ){
        try {
            log.debug("REST requested to edit a country: ", country);
            countryDao.editCountryDetail(countryCode, country);
            Country countryFromDb = countryDao.getCountryDetail(countryCode);
            return ResponseEntity.ok(countryFromDb);
        }catch(Exception ex) {
            System.out.println("Error while editing the country: {} with data: {}"+ countryCode+ country+ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while ediiting the country");
        }
    }

    @GetMapping("/continents")
    public ResponseEntity<?> getContinents(){
        try{
            return ResponseEntity.ok(lookupDAO.getContinents());
        } catch(Exception ex){
            System.out.println("Error while getting continents: {}"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting the continents");
        }
    }

    @GetMapping("/regions")
    public ResponseEntity<?> getRegions(){
        try {
            return ResponseEntity.ok(lookupDAO.getRegions());
        } catch(Exception ex){
            System.out.println("Error while getting regions: {}"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting the regions");
        }
    }

    @GetMapping("/headStates")
    public ResponseEntity<?> getHeadOfStates(){
        try {
            return ResponseEntity.ok(lookupDAO.getHeadOfStates());
        } catch(Exception ex){
            System.out.println("Error while getting head of states: {}"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting head of states");
        }
    }

    @GetMapping("/government")
    public ResponseEntity<?> getGovernmentForm(){
        try {
            return ResponseEntity.ok(lookupDAO.getGovernmentTypes());
        } catch(Exception ex){
            System.out.println("Error while getting government types: {}"+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while getting government types");
        }
    }
}
