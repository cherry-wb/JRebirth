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
package org.jrebirth.core.ui.simple;

import javafx.scene.Node;

import org.jrebirth.core.exception.CoreException;
import org.jrebirth.core.ui.AbstractModel;
import org.jrebirth.core.ui.Model;
import org.jrebirth.core.ui.NullView;

/**
 * 
 * The interface <strong>AbstractSimpleModel</strong>.
 * 
 * Base implementation for simple model without View-Controller part.
 * 
 * @author Sébastien Bordes
 * 
 * @param <N> the root node type
 */
public abstract class AbstractSimpleModel<N extends Node> extends AbstractModel<Model, NullView> {

    /** The root node. */
    private N rootNode;

    /**
     * Prepare the root node.
     * 
     * With simple model no view neither controller are created.<br />
     * You must manage them yourself.
     * 
     * @return the model root node
     */
    protected abstract N prepareNode();

    /**
     * Initialize the model.
     * 
     * @throws CoreException if the creation of the view fails
     */
    @Override
    protected void initInternalModel() throws CoreException {

        // Initiailze model with custom method
        initModel();

        // Prepare the root node
        this.rootNode = prepareNode();
    }

    @Override
    protected void bindObject() {
        // Nothing to do yet FIXME

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public N getRootNode() {
        return this.rootNode;
    }

}
