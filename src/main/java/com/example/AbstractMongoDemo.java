package com.example;

import com.example.model.Person;
import com.vaadin.ui.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.tylproject.vaadin.addon.MongoContainer;

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

    final protected MongoContainer.Builder<Person> mongoBuilder() {
        return MongoContainer.Builder
                .forEntity(Person.class, mongoOperations)
                .withProperty("firstName", String.class)
                .withProperty("lastName", String.class)
                .withNestedProperty("address.street", String.class)
                .withNestedProperty("address.zipCode", String.class)
                .withNestedProperty("address.city", String.class)
                .withNestedProperty("address.state", String.class)
                .withNestedProperty("address.country", String.class)
                .sortedBy(new Sort("lastName"));
    }

    protected AbstractMongoDemo initLayout() {
        try {

            mongoContainer = buildMongoContainer();
            table = new Table("Persons", mongoContainer);
            table.setSizeFull();
        } catch (Exception e){throw new Error(e);}


        this.setMargin(true);

        this.addComponent(addButtons(new HorizontalLayout()));


        logger.info(mongoContainer.getContainerPropertyIds().toString());

        table.setSelectable(true);
        this.addComponent(table);
        //table.setVisibleColumns("firstName", "lastName");


        initButtons();

        return this;
    }

    protected abstract MongoContainer<Person> buildMongoContainer() ;

    protected abstract void initButtons();

    protected void disable(Button... btns) {
        for (Button b: btns) b.setEnabled(false);
    }

    protected void enable(Button... btns) {
        for (Button b: btns) b.setEnabled(true);
    }


    protected abstract Layout addButtons(HorizontalLayout layout);


}
