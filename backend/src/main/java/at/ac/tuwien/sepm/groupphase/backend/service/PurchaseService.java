package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.PurchaseDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ConflictException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

public interface PurchaseService {

    /**
     * Purchase merchandises and/or buy tickets.
     *
     * @param purchaseDto item(s) to be purchased
     */
    void purchase(PurchaseDto purchaseDto) throws ValidationException, ConflictException;

    /**
     * Performs a purchase by a vendor to a customer with the specified email.
     *
     * @param purchaseDto includes the tickets and merchandise that are purchased
     * @param email       of the customer
     */
    void purchaseByVendorForCustomer(PurchaseDto purchaseDto, String email) throws ValidationException, ConflictException;
}
