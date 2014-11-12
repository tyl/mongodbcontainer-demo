package com.example;

import com.mongodb.MongoClient;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
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
public abstract class AbstractMongoDemo extends VerticalLayout {
    private Logger logger = Logger.getAnonymousLogger();
    MongoContainer<Person> mongoContainer;
    MongoOperations mongoOperations;
    Table table;

    final Button btnRemove = new Button("Remove");
    final Button btnEdit   = new Button("Edit");
    final Button btnAdd    = new Button("Add");


    public AbstractMongoDemo(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    protected AbstractMongoDemo initLayout() {
        try {

            mongoContainer = buildMongoContainer();
            table = new Table("Persons", mongoContainer);
        } catch (Exception e){throw new Error(e);}


        this.setMargin(true);

        this.addComponent(addButtons(new HorizontalLayout()));


        logger.info(mongoContainer.getContainerPropertyIds().toString());

        table.setSelectable(true);
        this.addComponent(table);
        table.setVisibleColumns("firstName", "lastName");

        final Class<?> c = mongoContainer.getItem(mongoContainer.firstItemId()).getClass();
        logger.info("class is "+c);

        initButtons();

        return this;
    }

    protected abstract  MongoContainer<Person> buildMongoContainer() ;

    protected abstract void initButtons();

    protected void disable(Button... btns) {
        for (Button b: btns) b.setEnabled(false);
    }

    protected void enable(Button... btns) {
        for (Button b: btns) b.setEnabled(true);
    }


    protected abstract Layout addButtons(HorizontalLayout layout);


}
