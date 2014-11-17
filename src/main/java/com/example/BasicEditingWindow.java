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
