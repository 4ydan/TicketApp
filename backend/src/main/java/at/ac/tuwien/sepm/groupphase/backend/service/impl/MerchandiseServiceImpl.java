package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.merchandise.MerchandiseRedeemDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.MerchandiseMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepm.groupphase.backend.entity.Merchandise;
import at.ac.tuwien.sepm.groupphase.backend.exception.AccountLockedException;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;
import at.ac.tuwien.sepm.groupphase.backend.repository.MerchandiseRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.UserRepository;
import at.ac.tuwien.sepm.groupphase.backend.service.MerchandiseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
public class MerchandiseServiceImpl implements MerchandiseService {

    private final MerchandiseRepository merchandiseRepository;

    private final MerchandiseMapper merchandiseMapper;

    private final MerchandiseValidator merchandiseValidator;

    private final UserRepository userRepository;

    @PersistenceContext
    EntityManager entityManager;

    public MerchandiseServiceImpl(MerchandiseRepository merchandiseRepository,
                                  MerchandiseMapper merchandiseMapper,
                                  MerchandiseValidator merchandiseValidator,
                                  UserRepository userRepository) {
        this.merchandiseRepository = merchandiseRepository;
        this.merchandiseMapper = merchandiseMapper;
        this.merchandiseValidator = merchandiseValidator;
        this.userRepository = userRepository;
    }

    @Override
    public List<MerchandiseDto> findAll(int page, String filter, String sort) {
        log.trace("Find all merchandises on page {} with filter {} and sort by {}", page, filter, sort);
        String filterPremium = "false";
        String filterNonPremium = "false";
        if (filter != null && filter.equals("premium")) {
            filterPremium = "true";
        } else if (filter != null && filter.equals("non-premium")) {
            filterNonPremium = "true";
        }
        if (sort != null && sort.equals("lowest-price")) {
            return merchandiseRepository.findAllOnPageSortByPriceAsc(page, filterPremium, filterNonPremium).stream()
                .map(merchandiseMapper::merchandiseToMerchandiseDto).toList();
        } else if (sort != null && sort.equals("highest-price")) {
            return merchandiseRepository.findAllOnPageSortByPriceDesc(page, filterPremium, filterNonPremium).stream()
                .map(merchandiseMapper::merchandiseToMerchandiseDto).toList();
        } else {
            return merchandiseRepository.findAllOnPage(page, filterPremium, filterNonPremium).stream().map(merchandiseMapper::merchandiseToMerchandiseDto)
                .toList();
        }
    }

    @Override
    @Transactional
    public void redeem(MerchandiseRedeemDto merchandise) throws ValidationException {
        log.trace("Purchase merchandises {}", merchandise);
        merchandiseValidator.validateForRedeeming(merchandise);
        ApplicationUser user = userRepository.getById(merchandise.getUserId());
        if (!user.getIsNotLocked()) {
            throw new AccountLockedException("Account " + user.getEmail() + " is locked");
        }
        Merchandise merch = merchandiseRepository.findById(merchandise.getItemId()).get();

        merch.setInStock(merch.getInStock() - merchandise.getAmount());

        int userBonusPoints = user.getBonusPoints();
        userBonusPoints -= (int) merch.getCostsPoints() * merchandise.getAmount();
        user.setBonusPoints(userBonusPoints);
        this.entityManager.merge(user);
    }
}
