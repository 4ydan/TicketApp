package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    /**
     * Finds all artist by name matching name.
     *
     * @param name that is matched
     * @return all matching artists.
     */
    List<Artist> findByNameContainingIgnoreCaseOrderByNameAsc(String name, Pageable pageable);

    /**
     * Finds an artist by id.
     *
     * @param id of the artist
     * @return the artist if he exists otherwise nothing.
     */
    @Override
    Optional<Artist> findById(Long id);

    /**
     * Finds an artist by name ignoring the case.
     *
     * @param name of the artist
     * @return the artist if he exists otherwise nothing.
     */
    Optional<Artist> findByNameIgnoreCase(String name);
}
