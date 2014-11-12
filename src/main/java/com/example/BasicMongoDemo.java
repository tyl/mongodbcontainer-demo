package com.example;

import com.mongodb.MongoClient;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.tylproject.vaadin.addon.MongoContainer;

import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created by evacchi on 11/11/14.
 */
public class BasicMongoDemo extends AbstractMongoDemo {
    public BasicMongoDemo(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    protected MongoContainer<Person> buildMongoContainer() {
        return MongoContainer.Builder
                .with(mongoOperations)
                .withBeanClass(Person.class)
                .build();
    }


    protected void initButtons() {
        disable(btnEdit, btnRemove);

        btnRemove.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object id = table.getValue();
                int index = mongoContainer.indexOfId(id);
                table.removeItem(id);
                table.select(mongoContainer.getIdByIndex(index));
            }
        });

        btnEdit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Object id = table.getValue();
                BeanItem<Person> item = mongoContainer.getItem(id);
                EditingWindow w =
                        new BasicEditingWindow(item, mongoOperations, table);
                UI.getCurrent().addWindow(w);
            }
        });

        btnAdd.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                EditingWindow w =
                        new BasicEditingWindow(new BeanItem<Person>(new Person()),
                                mongoOperations, table);
                UI.getCurrent().addWindow(w);
            }
        });

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (null == event.getProperty().getValue()) {
                    disable(btnEdit, btnRemove);
                } else {
                    enable(btnEdit, btnRemove);
                }
            }
        });

    }

    @Override
    protected Layout addButtons(HorizontalLayout layout) {
        layout.addComponent(btnAdd);
        layout.addComponent(btnEdit);
        layout.addComponent(btnRemove);

        return layout;
    }


}
