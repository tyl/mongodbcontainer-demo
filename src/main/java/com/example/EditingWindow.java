package com.example;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;

/**
 * Created by evacchi on 11/11/14.
 */
public abstract class EditingWindow extends Window {
    private final VerticalLayout layout;
    final protected Person person;
    final protected BeanFieldGroup<Person> fieldGroup;
    final protected Button btnOK = new Button("OK");
    final protected Button btnCancel = new Button("Cancel");
    final protected MongoOperations mongoOperations;
    final protected Table table;
    protected final BeanItem<Person> beanItem;


    public EditingWindow(
            final BeanItem<Person> item,
            final MongoOperations mongoOperations,
            final Table table) {
        super("Edit");
        this.mongoOperations = mongoOperations;
        this.table = table;
        center();
        setWidth("400px");
        setResizable(false);


        this.beanItem = item;
        this.person = item.getBean();
        this.layout = new VerticalLayout();
        this.layout.setMargin(true);

        this.fieldGroup = new BeanFieldGroup<Person>(Person.class);

        this.layout.addComponent(makeFormLayout(fieldGroup, item));
        this.layout.addComponent(makeFooter());


        this.setContent(this.layout);

        this.btnCancel.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                EditingWindow.this.close();
            }
        });

        this.btnOK.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                onOKButtonClick();
            }
        });

    }

    protected abstract void onOKButtonClick() ;

    protected FormLayout makeFormLayout(BeanFieldGroup<Person> fieldGroup, BeanItem<Person> item) {
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

    protected HorizontalLayout makeFooter() {
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