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

import javax.servlet.annotation.WebServlet;

import com.example.model.Person;
import com.mongodb.MongoClient;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.net.UnknownHostException;
import java.util.Arrays;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MongoDBContainerDemo extends UI
{



    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MongoDBContainerDemo.class)
    public static class Servlet extends VaadinServlet {}

    final TabSheet tabSheet = new TabSheet();
    MongoOperations mongoOperations = null;

    @Override
    protected void init(VaadinRequest request) {
        this.setContent(tabSheet);
        try {
            mongoOperations = new MongoTemplate(new MongoClient(), "scratch");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        // mongoOperations.remove(new Query(), Person.class);
        // generateRecords();


        final AbstractMongoDemo basic = new BasicMongoDemo(mongoOperations).initLayout();
        final AbstractMongoDemo buffered = new BufferedMongoDemo(mongoOperations).initLayout();

        tabSheet.addTab(basic, "Basic");
        tabSheet.addTab(buffered, "Buffered");

        tabSheet.setSelectedTab(basic);



        tabSheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent event) {
                if (tabSheet.getSelectedTab().equals(buffered)) {
                    buffered.mongoContainer.refresh();
                    buffered.table.refreshRowCache();
                } else {
                    basic.mongoContainer.refresh();
                    basic.table.refreshRowCache();
                }
            }
        });
    }

    protected void generateRecords() {
        Person[] ps = new Person[180];
        for (int j = 0; j<180; j+=4) {
            ps[j]= (new Person("arnold",  j   + ""));
            ps[j+1]=(new Person("andrew", j+1 + ""));
            ps[j+2]=(new Person("paul",   j+2 +  ""));
            ps[j+3]=(new Person("simon",  j+3 + ""));
        }
        mongoOperations.insert(Arrays.asList(ps), Person.class);
    }

}
