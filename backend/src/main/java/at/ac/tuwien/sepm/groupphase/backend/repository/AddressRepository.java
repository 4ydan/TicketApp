package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Delete user by id.
     *
     * @param id - user identifier
     */
    void deleteById(Long id);

    /**
     * Get unique countries.
     *
     * @return list of countries
     */
    @Query(value = "SELECT DISTINCT country FROM addresses", nativeQuery = true)
    List<String> findUniqueCountries();

    /**
     * Get unique postal codes.
     *
     * @return list of postal codes
     */
    @Query(value = "SELECT DISTINCT postal_code FROM addresses", nativeQuery = true)
    List<String> findUniquePostalCodes();

    /**
     * Get unique cities.
     *
     * @return list of cities
     */
    @Query(value = "SELECT DISTINCT city FROM addresses", nativeQuery = true)
    List<String> findUniqueCities();

}

