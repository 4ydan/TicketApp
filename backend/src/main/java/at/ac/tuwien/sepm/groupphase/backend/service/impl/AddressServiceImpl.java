package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.AddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;

    @Autowired
    public AddressServiceImpl(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @Override
    public List<String> getUniqueCountries() {
        log.trace("Get countries");
        return this.addressRepository.findUniqueCountries();
    }

    @Override
    public List<String> getUniquePostalCodes() {
        log.trace("Get postal codes");
        return this.addressRepository.findUniquePostalCodes();
    }

    @Override
    public List<String> getUniqueCities() {
        log.trace("Get cities");
        return this.addressRepository.findUniqueCities();
    }


}
