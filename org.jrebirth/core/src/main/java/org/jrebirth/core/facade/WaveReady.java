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
package org.jrebirth.core.facade;

import org.jrebirth.core.exception.WaveException;
import org.jrebirth.core.wave.Wave;
import org.jrebirth.core.wave.WaveData;
import org.jrebirth.core.wave.WaveType;
import org.jrebirth.core.wave.checker.WaveChecker;

/**
 * The interface <strong>WaveReady</strong>.
 * 
 * Define the contract used to manage waves.
 * 
 * @author Sébastien Bordes
 */
public interface WaveReady<R extends WaveReady<R>> extends FacadeReady<R> {

    /**
     * Begin to listen the type of wave for the current component.
     * 
     * Wave Contract will be checked if {@link org.jrebirth.core.resource.provided.JRebirthParameters.DEVELOPER_MODE} parameter is true
     * 
     * @param waveType the type(s) to listen
     */
    void listen(final WaveType... waveType);

    /**
     * Begin to listen the type of wave for the current component.
     * 
     * Wave Contract will be checked if {@link org.jrebirth.core.resource.provided.JRebirthParameters.DEVELOPER_MODE} parameter is true
     * 
     * The wave checker is used to filter the wave if the checker returns false
     * 
     * @param waveChecker the wave checker used to forward the wave only if the checker return true
     * @param waveType the type(s) to listen
     */
    void listen(final WaveChecker waveChecker, final WaveType... waveType);

    /**
     * Register a wave call back contract.
     * 
     * Wave Contract will be checked if {@link org.jrebirth.core.resource.provided.JRebirthParameters.DEVELOPER_MODE} parameter is true
     * 
     * @param callType the wave type mapped to this service.
     * @param responseType the wave type of the wave emitted in return
     */
    void registerCallback(final WaveType callType, final WaveType responseType);

    /**
     * Stop to listen the type of wave for the current component.
     * 
     * @param waveTypes the type(s) to stop to listen
     */
    void unlisten(final WaveType... waveTypes);

    /**
     * Send a wave to the notifier.
     * 
     * The wave will automatically be sent from JRebirthThread.
     * 
     * @param wave the wave to send
     */
    void sendWave(final Wave wave);

    /**
     * Send a wave to the notifier.
     * 
     * The wave will automatically be sent from JRebirthThread.
     * 
     * @param waveType the type of wave to send
     * @param waveData the data (key-value
     * 
     * @return the wave created and sent to JIT, be careful when you use a strong reference it can hold a lot of objects
     */
    Wave sendWave(final WaveType waveType, final WaveData<?>... waveData);

    /**
     * Return the return wave type.
     * 
     * @param waveType the source wave type
     * 
     * @return Returns the waveType for return wave.
     */
    WaveType getReturnWaveType(final WaveType waveType);

    /**
     * Process a wave.
     * 
     * @param wave the wave to process.
     * 
     * @throws WaveException if an error occurred while dispatching the wave
     */
    void handle(final Wave wave) throws WaveException;

}
