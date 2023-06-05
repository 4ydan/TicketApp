package at.ac.tuwien.sepm.groupphase.backend.service;

import java.util.List;

public interface AddressService {

    /**
     * Get all distinct Countries that are in the database.
     *
     * @return ordered list of all unique countries.
     */
    List<String> getUniqueCountries();

    /**
     * Get all distinct Postal Codes that are in the database.
     *
     * @return ordered list of all unique postal codes.
     */
    List<String> getUniquePostalCodes();

    /**
     * Get all distinct Cities that are in the database.
     *
     * @return ordered list of all unique cities.
     */
    List<String> getUniqueCities();

}
