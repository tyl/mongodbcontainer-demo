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
