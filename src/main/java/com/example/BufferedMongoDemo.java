package com.example;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.UI;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.BufferedMongoContainer;
import org.tylproject.vaadin.addon.MongoContainer;

/**
 * Created by evacchi on 11/11/14.
 */
public class BufferedMongoDemo extends AbstractMongoDemo {

    protected Button btnCommit = new Button("Commit");
    protected Button btnDiscard = new Button("Discard");
    protected BufferedMongoContainer<Person> mongoContainer;

    public BufferedMongoDemo(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    protected MongoContainer<Person> buildMongoContainer() {
        return mongoContainer = MongoContainer.Builder
                .with(mongoOperations)
                .withBeanClass(Person.class)
                .buildBuffered();
    }

    @Override
    protected AbstractMongoDemo initLayout() {
        return super.initLayout();
    }

    @Override
    protected Layout addButtons(HorizontalLayout layout) {
        layout.addComponent(btnAdd);
        layout.addComponent(btnEdit);
        layout.addComponent(btnRemove);
        layout.addComponent(btnCommit);
        layout.addComponent(btnDiscard);

        return layout;
    }

    @Override
    protected void initButtons() {

        disable(btnEdit, btnRemove);

        btnRemove.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Object id = table.getValue();
                table.removeItem(id);
            }
        });

        btnEdit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Object id = table.getValue();
                BeanItem<Person> item = mongoContainer.getItem(id);
                EditingWindow w = new BufferedEditingWindow(
                        (ObjectId)id, item, mongoOperations, mongoContainer, table);
                UI.getCurrent().addWindow(w);
            }
        });

        btnAdd.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ObjectId objectId = mongoContainer.addItem();
                BeanItem<Person> item = mongoContainer.getItem(objectId);
                EditingWindow w = new BufferedEditingWindow
                        (objectId, item, mongoOperations, mongoContainer, table);
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


        btnCommit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                // table.commit();
                mongoContainer.commit();
            }
        });
        btnDiscard.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                mongoContainer.discard();
            }
        });

    }
}