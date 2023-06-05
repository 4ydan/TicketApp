package at.ac.tuwien.sepm.groupphase.backend.service;

import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.CreateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.DetailedMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MessageDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.UpdateMessageDto;
import at.ac.tuwien.sepm.groupphase.backend.exception.ValidationException;

import java.util.List;

public interface MessageService {

    /**
     * Find all message entries on a specific page ordered by published at date (descending).
     *
     * @param page defines which entries will be fetched
     * @return ordered list of all message entries on a specific page
     */
    List<MessageDto> findAll(int page);


    /**
     * Find a single message entry by id.
     *
     * @param id the id of the message entry
     * @return the message entry
     */
    DetailedMessageDto findOne(Long id);

    /**
     * Find all read or unread message entries on a specific page ordered by published at date (descending).
     *
     * @param page defines which entries will be fetched
     * @param read defines whether to fetch the read or unread messages
     * @return ordered list of all message entries on a specific page
     */
    List<MessageDto> findReadOrUnread(boolean read, int page);

    /**
     * Delete a message entry by id.
     *
     * @param id the id of the message entry
     */
    void deleteById(long id);

    /**
     * Create a new message entry and save it in the persistent data store.
     *
     * @param toCreate Dto of message to create
     * @throws ValidationException if some provided parameters are not valid
     */
    DetailedMessageDto create(CreateMessageDto toCreate) throws ValidationException;

    /**
     * Updates an existing message in the persistent data store.
     *
     * @param toUpdate Dto of updated parameters for an existing message
     * @return the updated message as DetailedMessageDto
     * @throws ValidationException if some provided parameters are not valid
     */
    DetailedMessageDto update(UpdateMessageDto toUpdate) throws ValidationException;
}
