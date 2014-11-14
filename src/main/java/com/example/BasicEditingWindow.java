package com.example;

import com.example.model.Person;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Table;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.MongoContainer;

/**
 * Created by evacchi on 11/11/14.
 */
public class BasicEditingWindow extends EditingWindow {

    public BasicEditingWindow(
            final BeanItem<Person> item,
            final MongoOperations mongoOperations,
            final Table table) {
       super(item, mongoOperations, table);
    }


    @Override
    public void onOKButtonClick() {

        try {
            fieldGroup.commit();
            MongoContainer<Person> container = (MongoContainer<Person>) this.table.getContainerDataSource();
            container.addEntity(person);

            this.table.refreshRowCache();
            
            ObjectId id = person.getId();
            if (!table.getVisibleItemIds().contains(id)) {
                table.setCurrentPageFirstItemId(id);
            }
            table.select(id);


            this.close();
        } catch (FieldGroup.CommitException ex) {
            throw new RuntimeException(ex);
        }
    }
}
