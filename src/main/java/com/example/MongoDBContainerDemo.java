package com.example;

import javax.servlet.annotation.WebServlet;

import com.mongodb.MongoClient;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.ValoTheme;
import org.tylproject.vaadin.addon.MongoContainer;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.logging.Logger;

@Theme("mytheme")
@SuppressWarnings("serial")
public class MongoDBContainerDemo extends UI
{



    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = MongoDBContainerDemo.class, widgetset = "com.example.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    private Logger logger = Logger.getAnonymousLogger();
    private final MongoContainer<Person> mongoContainer;
    private final MongoOperations mongoOperations;
    final Table table;

    final Button btnRemove = new Button("Remove");
    final Button btnEdit   = new Button("Edit");
    final Button btnAdd    = new Button("Add");


    public MongoDBContainerDemo() {
        try {

            mongoOperations = new MongoTemplate(new MongoClient(), "database");
            mongoContainer = MongoContainer.Builder
                    .with(mongoOperations)
                    .withBeanClass(Person.class).build();
            table = new Table("Persons", mongoContainer);
        } catch (Exception e){throw new Error(e);}

    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        layout.addComponent(addButtons(new HorizontalLayout()));

        mongoOperations.remove(new Query(), Person.class);
        generateRecords();

        System.out.println(mongoContainer.getContainerPropertyIds());

        table.setSelectable(true);
        layout.addComponent(table);
        table.setVisibleColumns("firstName", "lastName");

        final Class<?> c = mongoContainer.getItem(mongoContainer.firstItemId()).getClass();
        logger.info("class is "+c);


        disable(btnEdit, btnRemove);

        btnRemove.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                Object id = table.getValue();
                table.removeItem(id);
            }
        });

        btnEdit.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Object id = table.getValue();
                BeanItem<Person> item = mongoContainer.getItem(id);
                EditingWindow w = new EditingWindow(item);
                MongoDBContainerDemo.this.addWindow(w);
            }
        });

        btnAdd.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                EditingWindow w = new EditingWindow(new BeanItem<Person>(new Person()));
                MongoDBContainerDemo.this.addWindow(w);
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

    private void disable(Button... btns) {
        for (Button b: btns) b.setEnabled(false);
    }

    private void enable(Button... btns) {
        for (Button b: btns) b.setEnabled(true);
    }


    private Layout addButtons(HorizontalLayout layout) {
        layout.addComponent(btnAdd);
        layout.addComponent(btnEdit);
        layout.addComponent(btnRemove);

        return layout;
    }

    private void generateRecords() {
        Person[] ps = new Person[180];
        for (int j = 0; j<180; j+=4) {
            ps[j]= (new Person("arnold", "" + j));
            ps[j+1]=(new Person("andrew", "" + j));
            ps[j+2]=(new Person("paul", "" + j));
            ps[j+3]=(new Person("simon", "" + j));
        }
        mongoOperations.insert(Arrays.asList(ps), Person.class);
    }

    private void generateOneMillionRecords() {
        for (int i = 0; i<200; i++) {
            Person[] ps = new Person[4*1000];
            for (int j = 0; j<4000; j+=4) {
                ps[j]= (new Person("arnold", "" + i*j));
                ps[j+1]=(new Person("andrew", "" + i*j));
                ps[j+2]=(new Person("paul", "" + i*j));
                ps[j+3]=(new Person("simon", "" + i*j));
            }
            mongoOperations.insert(Arrays.asList(ps), Person.class);
        }
    }


    class EditingWindow extends Window {
        private final VerticalLayout layout;
        final private Person person;
        final private BeanFieldGroup<Person> fieldGroup;
        final private Button btnOK = new Button("OK");
        final private Button btnCancel = new Button("Cancel");


        EditingWindow(BeanItem<Person> item) {
            super("Edit");
            center();
            setWidth("400px");
            setResizable(false);


            this.person = item.getBean();
            this.layout = new VerticalLayout();
            this.layout.setMargin(true);


            this.fieldGroup = new BeanFieldGroup<Person>(Person.class);

            this.layout.addComponent(makeFormLayout(fieldGroup, item));
            this.layout.addComponent(makeFooter());


            this.setContent(this.layout);


            this.btnOK.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    try {
                        fieldGroup.commit();
                        mongoOperations.save(person);
                        table.refreshRowCache();

                        ObjectId id = person.getId();
                        table.setCurrentPageFirstItemId(id);
                        table.select(id);


                        EditingWindow.this.close();
                    } catch (FieldGroup.CommitException e) {
                        throw new RuntimeException(e);
                    }
                }
            });



            this.btnCancel.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    EditingWindow.this.close();
                }
            });

        }

        FormLayout makeFormLayout(BeanFieldGroup<Person> fieldGroup, BeanItem<Person> item) {
            FormLayout formLayout = new FormLayout();
            formLayout.setMargin(true);
            formLayout.setHeightUndefined();

            fieldGroup.setItemDataSource(person);
            for (Object pid: item.getItemPropertyIds()) {
                if (pid.equals("id")) continue;
                formLayout.addComponent(fieldGroup.buildAndBind(pid));
            }

            return formLayout;
        }

        HorizontalLayout makeFooter() {
            HorizontalLayout footer = new HorizontalLayout();
            footer.setWidth("100%");
            footer.setSpacing(true);
            footer.addStyleName("v-window-bottom-toolbar");

            Label footerText = new Label("Footer text");
            footerText.setSizeUndefined();

            btnOK.addStyleName(ValoTheme.BUTTON_PRIMARY);
            btnOK.setClickShortcut(ShortcutAction.KeyCode.ENTER);

            btnCancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);

            footer.addComponents(footerText, btnOK, btnCancel);
            footer.setExpandRatio(footerText, 1);

            return footer;
        }



    }

}
