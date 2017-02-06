package com.vaadin.addon.daterangefield;

import java.time.LocalDate;
import java.util.function.Consumer;

import com.vaadin.server.Resource;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.DateField;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class DateRangeField extends CustomField<DateRange> {

    public static class DateRangeShortcut {

        private final DateRangeField dateRangeField;

        private final MenuItem menuItem;

        private DateRangeShortcut(DateRangeField dateRangeField, MenuItem menuItem) {
            this.dateRangeField = dateRangeField;
            this.menuItem = menuItem;
        }

        public boolean isEnabled() {
            return this.menuItem.isEnabled();
        }

        public void setEnabled(boolean enabled) {
            this.menuItem.setEnabled(enabled);
        }

        public DateRangeShortcut addShortcut(String caption, Resource icon, Consumer<DateRangeField> action) {
            return new DateRangeShortcut(this.dateRangeField,
                    this.menuItem.addItem(caption, icon, item -> action.accept(this.dateRangeField)));
        }

        public DateRangeShortcut addShortcut(String caption, Consumer<DateRangeField> action) {
            return this.addShortcut(caption, null, action);
        }

        public DateRangeShortcut addShortcut(String caption) {
            return this.addShortcut(caption, null, field -> {
            });
        }

        public void addShortcutSeparator() {
            this.menuItem.addSeparator();
        }

    }

    private final CssLayout root;

    private final DateField beginDateField;

    private final DateField endDateField;

    private final MenuBar menuBar;

    private final MenuItem menuItem;

    private Registration beginDateFieldListenerRegistration;

    private Registration endDateFieldListenerRegistration;

    public DateRangeField() {
        this.beginDateField = new DateField();
        this.endDateField = new DateField();
        this.menuBar = new MenuBar();
        this.menuItem = this.menuBar.addItem("", null);
        this.root = new CssLayout(this.beginDateField, this.endDateField);
        this.root.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        this.beginDateField.addValueChangeListener(change -> {
            DateRange oldValue = this.getValue();
            LocalDate beginDate = change.getValue();
            if (beginDate != null) {
                LocalDate endDate = this.getEndDate();
                if (endDate != null && endDate.compareTo(beginDate) < 0) {
                    this.endDateFieldListenerRegistration.remove();
                    this.endDateField.clear();
                    this.endDateFieldListenerRegistration = this.endDateField.addValueChangeListener(event -> {
                        LocalDate oldEndDate = event.getOldValue();
                        this.fireEvent(new ValueChangeEvent<>(this,
                                DateRange.between(this.getBeginDate(), oldEndDate),
                                event.isUserOriginated()));
                    });
                }
            }
            this.endDateField.setRangeStart(beginDate);
        });
        this.attachValueChangeListeners();
    }

    public DateRangeField(String caption) {
        this();
        this.setCaption(caption);
    }

    public DateRangeShortcut addShortcut(String caption, Resource icon, Consumer<DateRangeField> action) {
        if (this.menuBar.getParent() == null) {
            this.root.addComponent(this.menuBar);
        }
        return new DateRangeShortcut(this, this.menuItem.addItem(caption, icon, item -> action.accept(this)));
    }

    public DateRangeShortcut addShortcut(String caption, Consumer<DateRangeField> action) {
        return this.addShortcut(caption, null, action);
    }

    public void addShortcutSeparator() {
        this.menuItem.addSeparator();
    }

    public DateField getBeginDateField() {
        return this.beginDateField;
    }

    public DateField getEndDateField() {
        return this.endDateField;
    }

    public LocalDate getBeginDate() {
        return this.beginDateField.getValue();
    }

    public void setBeginDate(LocalDate date) {
        this.beginDateField.setValue(date);
    }

    public LocalDate getEndDate() {
        return this.endDateField.getValue();
    }

    public void setEndDate(LocalDate date) {
        this.endDateField.setValue(date);
    }

    public void setRange(DateRange dateRange) {
        this.setRange(dateRange.getBeginDate(), dateRange.getEndDate());
    }

    public void setRange(LocalDate beginDate, LocalDate endDate) {
        DateRange oldValue = this.getValue();
        this.detachValueChangeListeners();
        this.setBeginDate(beginDate);
        this.setEndDate(endDate);
        this.attachValueChangeListeners();
        this.fireEvent(new ValueChangeEvent<>(this, oldValue, false));
    }

    @Override
    public DateRange getValue() {
        LocalDate beginDate = this.getBeginDate();
        LocalDate endDate = this.getEndDate();
        return DateRange.between(beginDate, endDate);
    }

    @Override
    public void clear() {
        DateRange oldValue = this.getValue();
        this.detachValueChangeListeners();
        this.beginDateField.clear();
        this.endDateField.clear();
        this.attachValueChangeListeners();
        this.fireEvent(new ValueChangeEvent<>(this, oldValue, false));
    }

    @Override
    public boolean isEmpty() {
        return this.beginDateField.isEmpty() && this.endDateField.isEmpty();
    }

    @Override
    protected void doSetValue(DateRange value) {
        this.setRange(value);
    }

    @Override
    protected Component initContent() {
        return this.root;
    }

    private void attachValueChangeListeners() {
        this.beginDateFieldListenerRegistration = this.beginDateField.addValueChangeListener(event -> {
            LocalDate oldBeginDate = event.getOldValue();
            this.fireEvent(new ValueChangeEvent<>(this,
                    DateRange.between(oldBeginDate, this.getEndDate()),
                    event.isUserOriginated()));
        });
        this.endDateFieldListenerRegistration = this.endDateField.addValueChangeListener(event -> {
            LocalDate oldEndDate = event.getOldValue();
            this.fireEvent(new ValueChangeEvent<>(this,
                    DateRange.between(this.getBeginDate(), oldEndDate),
                    event.isUserOriginated()));
        });
    }

    private void detachValueChangeListeners() {
        this.beginDateFieldListenerRegistration.remove();
        this.endDateFieldListenerRegistration.remove();
    }

}
