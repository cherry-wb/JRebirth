package org.jrebirth.core.ui.fxml;

import javafx.util.Callback;

import org.jrebirth.core.log.JRLogger;
import org.jrebirth.core.log.JRLoggerFactory;
import org.jrebirth.core.ui.Model;
import org.jrebirth.core.ui.View;

/**
 * The class <strong>DefaultFXMLControllerBuilder</strong>.
 * 
 * @author Sébastien Bordes
 */
public class DefaultFXMLControllerBuilder implements Callback<Class<?>, Object> {

    /** The class logger. */
    private static final JRLogger LOGGER = JRLoggerFactory.getLogger(DefaultFXMLControllerBuilder.class);

    /** The root model of the FXML file. */
    private final Model relatedModel;

    /**
     * Default Constructor.
     * 
     * @param relatedModel the root model for fx/include or hte main model
     */
    public DefaultFXMLControllerBuilder(final Model relatedModel) {
        this.relatedModel = relatedModel;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object call(final Class<?> controllerClass) {
        FXMLController<Model, View<Model, ?, ?>> controller = null;
        try {

            controller = (FXMLController<Model, View<Model, ?, ?>>) controllerClass.newInstance();
            controller.setModel(this.relatedModel);

        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.log(FXMLMessages.DEFAULT_CTRLR_CREATION_ERROR, e, e.getMessage());
        }

        return controller;
    }
}
