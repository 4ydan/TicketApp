package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.PerformanceDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

public interface PerformanceService {
    /**
     * Get a performance by id.
     *
     * @param id the id of the performance
     * @return a performance dto
     */
    PerformanceDto getById(long id);

    /**
     * Create a new performance entry and save it in the persistent data store.
     *
     * @param toCreate Dto of performance to create
     * @param eventId of the corresponding event
     * @throws ValidationException if some provided parameters are not valid
     */
    PerformanceDto create(PerformanceDto toCreate, Long eventId, Long hallPlanId) throws ValidationException;
}
