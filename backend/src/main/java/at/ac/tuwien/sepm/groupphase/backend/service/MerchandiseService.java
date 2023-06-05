package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseRedeemDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface MerchandiseService {

    /**
     * Find all merchandise entries on a specific page.
     *
     * @param page   defines which entries will be fetched
     * @param filter defines which entries will be fetched
     * @param sort   defines order of fetched elements
     * @return list of all merchandise entries on a specific page
     */
    List<MerchandiseDto> findAll(int page, String filter, String sort);

    /**
     * Redeem merchandise.
     *
     * @param merchandise to redeem
     * @throws ValidationException in case of an error
     */
    void redeem(MerchandiseRedeemDto merchandise) throws ValidationException;
}
