package com.naval.monitoring.service;

import com.naval.monitoring.exception.CommunicationException;
import com.naval.monitoring.exception.VesselNotFoundException;
import com.naval.monitoring.model.Message;
import com.naval.monitoring.model.MessagePriority;
import com.naval.monitoring.repository.MessageRepository;
import com.naval.monitoring.util.FileLogger;
import com.naval.monitoring.util.InputValidator;
import com.naval.monitoring.util.SimpleCipher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Business logic for the Communication module: sending and retrieving
 * inter-vessel messages. Message bodies are encrypted before persistence
 * (security NFR) and decrypted only when explicitly read.
 */
public class CommunicationService {
    private final MessageRepository messageRepository;
    private final VesselService vesselService;

    public CommunicationService(MessageRepository messageRepository, VesselService vesselService) {
        this.messageRepository = messageRepository;
        this.vesselService = vesselService;
    }

    public Message sendMessage(String fromVesselId, String toVesselId, String body, MessagePriority priority)
            throws VesselNotFoundException, CommunicationException {
        if (InputValidator.isBlank(body)) {
            throw new CommunicationException("Message body cannot be blank");
        }
        vesselService.getVessel(fromVesselId);
        vesselService.getVessel(toVesselId);

        String encrypted = SimpleCipher.encrypt(body);
        Message message = new Message(UUID.randomUUID().toString().substring(0, 8),
                fromVesselId, toVesselId, encrypted, priority, LocalDateTime.now());
        messageRepository.save(message);
        FileLogger.info(String.format("Message sent %s -> %s [%s]", fromVesselId, toVesselId, priority));
        return message;
    }

    public List<Message> getInbox(String vesselId) {
        return messageRepository.findByRecipient(vesselId);
    }

    public String readMessage(Message message) {
        return SimpleCipher.decrypt(message.getEncryptedBody());
    }
}
