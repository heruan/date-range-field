package com.vaadin.addon.daterangefield.demo;

import java.time.DayOfWeek;
import java.time.LocalDate;

import javax.servlet.annotation.WebServlet;

import com.vaadin.addon.daterangefield.DateRangeField;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("demo")
@Title("DateRangeField Add-on Demo")
@SuppressWarnings("serial")
public class DemoUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = DemoUI.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        final DateRangeField component = new DateRangeField("Date Range Field");

        component.getBeginDateField()
                 .setPlaceholder("Begin date");
        component.getEndDateField()
                 .setPlaceholder("End date");

        LocalDate today = LocalDate.now();
        component.addShortcut("Today", field -> {
            field.setRange(today, today);
        })
                 .addShortcut("plus one year", field -> {
                     field.setRange(today, today.plusYears(1));
                 });
        component.addShortcut("This week", field -> {
            field.setRange(today.with(DayOfWeek.MONDAY), today.with(DayOfWeek.SUNDAY));
        });
        component.addShortcut("Next week", field -> {
            field.setRange(today.with(DayOfWeek.MONDAY)
                                .plusWeeks(1),
                           today.with(DayOfWeek.SUNDAY)
                                .plusWeeks(1));
        });
        component.addShortcutSeparator();
        component.addShortcut("Clear", field -> field.clear());

        Label period = new Label();

        component.addValueChangeListener(change -> {
            period.setValue(change.getValue()
                                  .getPeriod()
                                  .toString());
        });

        component.setSizeUndefined();

        final VerticalLayout layout = new VerticalLayout();
        layout.setStyleName("demoContentLayout");
        layout.setSizeFull();
        layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        VerticalLayout container = new VerticalLayout(component, period);
        container.setSizeUndefined();
        layout.addComponents(container);
        this.setContent(layout);
    }
}
