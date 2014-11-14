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
    @VaadinServletConfiguration(productionMode = false, ui = MongoDBContainerDemo.class, widgetset = "com.example.AppWidgetSet")
    public static class Servlet extends VaadinServlet {}

    final TabSheet tabSheet = new TabSheet();
    MongoOperations mongoOperations = null;

    @Override
    protected void init(VaadinRequest request) {
        this.setContent(tabSheet);
        try {
            mongoOperations = new MongoTemplate(new MongoClient(), "database");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        mongoOperations.remove(new Query(), Person.class);
        //generateRecords();


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
                } else {
                    basic.mongoContainer.refresh();
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
