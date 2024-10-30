package com.erebelo.springmongodbdemo.context.history;

import com.erebelo.springmongodbdemo.util.HttpHeadersUtil;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class DocumentHistoryService {

    private final MongoTemplate mongoTemplate;

    public static final String OBJECT_ID = "_id";
    public static final String VERSION = "version";

    public void saveChangeHistory(Document document, Object source) {
        if (isToSaveHistory(source.getClass())) {
            var historyDocument = new Document(document);
            var objectId = historyDocument.getObjectId(OBJECT_ID);
            historyDocument.remove(OBJECT_ID);

            var version = historyDocument.getLong(VERSION);
            var actionEnum = version == null || version == 0 ? HistoryActionEnum.INSERT : HistoryActionEnum.UPDATE;

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

    private void createAndSaveHistoryDocument(HistoryActionEnum actionEnum, String documentId, String collectionName,
            Document document) {
        var history = new Document().append("action", actionEnum.getValue()).append("documentId", documentId);

        if (HistoryActionEnum.DELETE.equals(actionEnum)) {
            history.append("historyCreatedBy", HttpHeadersUtil.getLoggedInUser());
            history.append("historyCreatedDateTime", Date.from(Instant.now()));
        } else {
            history.put("document", document);
        }

        mongoTemplate.insert(history, collectionName);
    }

    private boolean isToSaveHistory(Class<?> clazz) {
        try {
            return clazz.isAnnotationPresent(DocumentHistory.class);
        } catch (NullPointerException e) {
            log.error("Error verifying whether Document contains @DocumentHistory annotation");
            throw e;
        }
    }

    private String getCollectionName(Class<?> clazz) {
        return clazz.getAnnotation(DocumentHistory.class).collection();
    }
}
