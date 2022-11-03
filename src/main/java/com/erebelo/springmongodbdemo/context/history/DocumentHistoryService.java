package com.erebelo.springmongodbdemo.context.history;

import com.erebelo.springmongodbdemo.utils.AuthenticationUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class DocumentHistoryService {

    public static final String OBJECT_ID = "_id";
    public static final String VERSION = "version";
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentHistoryService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    public void saveChangeHistory(Document document, Object source) {
        if (isToSaveHistory(source.getClass())) {
            Document historyDocument = new Document(document);
            ObjectId objectId = historyDocument.getObjectId(OBJECT_ID);
            historyDocument.remove(OBJECT_ID);

            Long version = historyDocument.getLong(VERSION);
            HistoryActionEnum actionEnum = version == null || version == 0 ? HistoryActionEnum.INSERT :
                    HistoryActionEnum.UPDATE;

            createAndSaveHistoryDocument(actionEnum, objectId.toString(), getCollectionName(source.getClass()),
                    historyDocument);
        }
    }

    public void saveDeleteHistory(Document document, Class<?> clazz) {
        if (isToSaveHistory(clazz)) {
            createAndSaveHistoryDocument(HistoryActionEnum.DELETE, document.getObjectId(OBJECT_ID).toString(),
                    getCollectionName(clazz), null);
        }
    }

    private boolean isToSaveHistory(Class<?> clazz) {
        try {
            return clazz.isAnnotationPresent(DocumentHistory.class);
        } catch (NullPointerException e) {
            LOGGER.error("Error verifying whether Document contains @DocumentHistory annotation", e);
            throw e;
        }
    }

    private String getCollectionName(Class<?> clazz) {
        try {
            return clazz.getAnnotation(DocumentHistory.class).collection();
        } catch (NullPointerException e) {
            LOGGER.error("Something went wrong when trying to get the history collection name", e);
            throw e;
        }
    }

    private void createAndSaveHistoryDocument(HistoryActionEnum actionEnum, String documentId, String collectionName,
            Document document) {
        Document history = new Document()
                .append("action", actionEnum.getValue())
                .append("documentId", documentId);

        if (HistoryActionEnum.DELETE.equals(actionEnum)) {
            history.append("historyCreatedBy", AuthenticationUtils.getLoggedInUser());
            history.append("historyCreatedDateTime", Date.from(Instant.now()));
        } else {
            history.put("document", document);
        }

        mongoTemplate.insert(history, collectionName);
    }
}
