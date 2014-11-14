/**
 * Copyright (c) 2014 - Marco Pancotti, Edoardo Vacchi and Daniele Zonca
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
import com.vaadin.ui.*;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.MongoContainer;

/**
 * Created by evacchi on 11/11/14.
 */
public class BasicMongoDemo extends AbstractMongoDemo {
    public BasicMongoDemo(MongoOperations mongoOperations) {
        super(mongoOperations);
    }

    @Override
    protected MongoContainer<Person> buildMongoContainer() {
        return mongoBuilder().build();
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
                BeanItem<Person> beanItem =
                        new BeanItem<Person>(new Person(), "firstName", "lastName");
                beanItem.addNestedProperty("address.street");
                beanItem.addNestedProperty("address.zipCode");
                beanItem.addNestedProperty("address.city");
                beanItem.addNestedProperty("address.state");
                beanItem.addNestedProperty("address.country");
                EditingWindow w =
                        new BasicEditingWindow(beanItem,
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
