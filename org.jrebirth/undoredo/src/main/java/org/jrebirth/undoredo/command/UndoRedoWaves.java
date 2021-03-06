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
package org.jrebirth.undoredo.command;

import org.jrebirth.core.wave.WaveItem;

/**
 * The Interface UndoRedoWaves used to define WaveItems used by undo/Redo waves.
 */
public interface UndoRedoWaves {

    /** The name ot the undeo/redo stack to use. */
    WaveItem<String> STACK_NAME = new WaveItem<String>() {
    };

    /** The undo redo flag to indicate if we must process the {@link AbstractUndoableCommand}.undo method or {@link AbstractUndoableCommand}.redo. */
    WaveItem<Boolean> UNDO_REDO = new WaveItem<Boolean>() {
    };

    /** The {@link Undoable} command. */
    WaveItem<Undoable> UNDOABLE_COMMAND = new WaveItem<Undoable>() {
    };

}
