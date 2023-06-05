package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventCreateDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDetailDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.event.EventSearchDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface EventService {

    /**
     * Find all event entries by the search criteria inside the searchDto sorted by startDate descending.
     *
     * @param searchDto the search parameters.
     * @return ordered list of all event entries.
     */
    List<EventDto> findAllByDate(EventSearchDto searchDto);

    /**
     * Find all event entries by the search criteria inside the searchDto sorted by title descending.
     *
     * @param searchDto the search parameters.
     * @return ordered list of all event entries.
     */
    List<EventDto> findAllByTitle(EventSearchDto searchDto);


    /**
     * Find event entries matching the title by a term and max amount defined by size.
     *
     * @param searchDto the search parameters.
     * @return ordered list of all event entries.
     */
    List<EventDto> findAllByTerm(EventSearchDto searchDto);

    /**
     * Find top ten events from a category.
     * The top ten are decided by tickets sold in a month.
     * Additional filtering can be performed, such as duration, description etc..
     *
     * @param searchDto a dto with the search criteria.
     * @return ordered list of all event entries.
     */
    List<EventDto> findTopTen(EventSearchDto searchDto);

    /**
     * Get an event by id.
     *
     * @param id the id of the event.
     * @return ordered list of all event entries.
     */
    EventDetailDto getById(long id);

    /**
     * Create a new event entry and save it in the persistent data store.
     *
     * @param toCreate Dto of event to create.
     * @throws ValidationException if some provided parameters are not valid.
     */
    EventCreateDetailDto create(EventCreateDto toCreate) throws ValidationException;

}
