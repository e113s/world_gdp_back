package com.elles.rest.controller;

import com.elles.rest.dao.CityDAO;
import com.elles.rest.model.City;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    private CityDAO cityDAO;

    private Logger log = LoggerFactory.getLogger(CityController.class);

    @GetMapping("/{countryCode}")
    public ResponseEntity<?> getCities(@PathVariable String countryCode,
                                       @RequestParam(name="pageNo", defaultValue="1") Integer pageNo){
        try {
            return new ResponseEntity<>(cityDAO.getCities(countryCode, pageNo),
                    HttpStatus.OK);
        }catch(Exception ex) {
            System.out.println("Error while getting cities for country: {}"+
                    countryCode+ ex);
            return new ResponseEntity<>("Error while getting cities",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping("/{countryCode}")
    public ResponseEntity<?> addCity(@PathVariable String countryCode,
                                     @Valid @RequestBody City city){
        try {
            log.debug("REST requested to add new city: ", city);
            cityDAO.addCity(countryCode, city);
            return new ResponseEntity<>(city, HttpStatus.CREATED);
        }catch(Exception ex) {
            System.out.println("Error while adding city: {} to country: {}"+
                    city+ countryCode+ ex);
            return new ResponseEntity<>("Error while adding city",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{cityId}")
    public ResponseEntity<?> deleteCity(@PathVariable Long cityId){
        try {
            log.debug("REST requested to delete a city: ", cityId);
            cityDAO.deleteCity(cityId);
            return ResponseEntity.ok().build();
        }catch(Exception ex) {
            System.out.println("Error occurred while deleting city : {}"+ cityId+ ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while deleting the city: " + cityId);
        }
    }
}
