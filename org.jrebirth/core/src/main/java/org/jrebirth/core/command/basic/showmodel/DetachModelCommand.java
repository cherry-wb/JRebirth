/**
 * Get more info at : www.jrebirth.org .
 * Copyright JRebirth.org © 2011-2013
 * Contact : sebastien.bordes@jrebirth.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jrebirth.core.command.basic.showmodel;

import javafx.scene.Node;

import org.jrebirth.core.command.DefaultUIBeanCommand;
import org.jrebirth.core.ui.Model;
import org.jrebirth.core.wave.Wave;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class <strong>DetachModelCommand</strong>.
 * 
 * @author Sébastien Bordes
 */
public class DetachModelCommand extends DefaultUIBeanCommand<DisplayModelWaveBean> {

    /** The class logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DetachModelCommand.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void execute(final Wave wave) {

        Model hideModel = getWaveBean(wave).getHideModel();
        if (hideModel == null && getWaveBean(wave).getHideModelKey() != null) {
            hideModel = getLocalFacade().getGlobalFacade().getUiFacade().retrieve(getWaveBean(wave).getHideModelKey());
        }

        Node hideNode = null;

        if (hideModel == null) {
            LOGGER.warn("Impossible to dettach a model because hideModel is null");
        } else {
            hideModel.doHideView(wave);
            hideNode = hideModel.getRootNode();

            if (hideNode == null) {
                LOGGER.warn("Impossible to dettach model {} because the node is null", hideModel.getClass().getSimpleName());
            } else {
                if (getWaveBean(wave).getUniquePlaceHolder() != null) {
                    getWaveBean(wave).getUniquePlaceHolder().set(null);
                } else if (getWaveBean(wave).getChidrenPlaceHolder() != null) {
                    getWaveBean(wave).getChidrenPlaceHolder().remove(hideNode);
                } else {
                    LOGGER.warn("Impossible to detach model {}, no place holder found", hideModel.getClass().getSimpleName());
                }
            }
        }

    }

    /**
     * Get the wave bean and cast it.
     * 
     * @param wave the wave that hold the bean
     * 
     * @return the casted wave bean
     */
    @Override
    public DisplayModelWaveBean getWaveBean(final Wave wave) {
        return (DisplayModelWaveBean) wave.getWaveBean();
    }

}
