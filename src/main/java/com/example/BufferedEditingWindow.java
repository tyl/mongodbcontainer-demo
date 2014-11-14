package com.example;

import com.example.model.Person;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.BufferedMongoContainer;

/**
 * Created by evacchi on 11/11/14.
 */
public class BufferedEditingWindow extends EditingWindow {

    private final BufferedMongoContainer<Person> mongoContainer;
    private final ObjectId itemId;

    public BufferedEditingWindow(
            final ObjectId itemId,
            final BeanItem<Person> item,
            final MongoOperations mongoOperations,
            final BufferedMongoContainer<Person> mongoContainer,
            final Table table) {
        super(item, mongoOperations, table);
        this.itemId = itemId;
        this.mongoContainer = mongoContainer;
        table.setBuffered(true);
    }


    @Override
    public void onOKButtonClick() {

        try {
            fieldGroup.commit();
            // do not store it back to Mongo, yet
            // this.mongoOperations.save(person);
            // notify the container about the change
            this.mongoContainer.notifyItemUpdated(itemId, beanItem);
            this.table.refreshRowCache();

            table.setCurrentPageFirstItemId(itemId);
            table.select(itemId);

            this.close();
        } catch (FieldGroup.CommitException ex) {
            throw new RuntimeException(ex);
        }
    }
}
