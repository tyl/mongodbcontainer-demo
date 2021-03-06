/**
 * Copyright (c) 2014 - Tyl Consulting s.a.s.
 *
 *    Authors: Edoardo Vacchi
 *    Contributors: Marco Pancotti, Daniele Zonca
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


package com.example;

import com.example.model.Person;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.BufferedMongoContainer;
import org.tylproject.vaadin.addon.MongoContainer;


public class BufferedMongoDemo extends AbstractMongoDemo {

    protected Button btnCommit = new Button("Commit");
    protected Button btnDiscard = new Button("Discard");
    protected BufferedMongoContainer<Person> mongoContainer;
    protected Notification msgCommit = new Notification("Changes Committed.", Notification.Type.TRAY_NOTIFICATION);
    protected Notification msgDiscard = new Notification("Changes Discarded.", Notification.Type.TRAY_NOTIFICATION);

    public BufferedMongoDemo(MongoOperations mongoOperations) {
        super(mongoOperations);
        msgCommit.setStyleName("system success");
        msgCommit.setPosition(Position.TOP_CENTER);
        msgCommit.setDelayMsec(1000);
        msgDiscard.setStyleName("system success");
        msgDiscard.setPosition(Position.TOP_CENTER);
        msgDiscard.setDelayMsec(1000);
    }

    @Override
    protected MongoContainer<Person> buildMongoContainer() {
        return mongoContainer = mongoBuilder().buildBuffered();
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
                Object id = table.getValue();
                if (id != null && !table.getVisibleItemIds().contains(id)) {
                    table.setCurrentPageFirstItemId(id);
                }
                msgCommit.show(UI.getCurrent().getPage());
            }
        });
        btnDiscard.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                mongoContainer.discard();
                msgDiscard.show(UI.getCurrent().getPage());
            }
        });

    }
}
