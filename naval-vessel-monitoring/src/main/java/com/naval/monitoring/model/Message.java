package com.naval.monitoring.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** An inter-vessel communication message, stored in encrypted form. */
public class Message {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String messageId;
    private final String fromVesselId;
    private final String toVesselId;
    private final String encryptedBody;
    private final MessagePriority priority;
    private final LocalDateTime sentAt;

    public Message(String messageId, String fromVesselId, String toVesselId, String encryptedBody,
                    MessagePriority priority, LocalDateTime sentAt) {
        this.messageId = messageId;
        this.fromVesselId = fromVesselId;
        this.toVesselId = toVesselId;
        this.encryptedBody = encryptedBody;
        this.priority = priority;
        this.sentAt = sentAt;
    }

    public String getMessageId() { return messageId; }
    public String getFromVesselId() { return fromVesselId; }
    public String getToVesselId() { return toVesselId; }
    public String getEncryptedBody() { return encryptedBody; }
    public MessagePriority getPriority() { return priority; }
    public LocalDateTime getSentAt() { return sentAt; }

    public String toCsv() {
        return String.join(",", messageId, fromVesselId, toVesselId, encryptedBody, priority.name(), sentAt.format(FMT));
    }

    public static Message fromCsv(String line) {
        String[] p = line.split(",", -1);
        return new Message(p[0], p[1], p[2], p[3], MessagePriority.valueOf(p[4]), LocalDateTime.parse(p[5], FMT));
    }
}
