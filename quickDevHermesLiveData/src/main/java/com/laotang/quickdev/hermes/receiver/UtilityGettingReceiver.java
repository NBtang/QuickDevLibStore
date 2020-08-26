/**
 *
 * Copyright 2016 Xiaofei
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.laotang.quickdev.hermes.receiver;

import com.laotang.quickdev.hermes.util.HermesException;
import com.laotang.quickdev.hermes.util.TypeUtils;
import com.laotang.quickdev.hermes.wrapper.MethodWrapper;
import com.laotang.quickdev.hermes.wrapper.ObjectWrapper;
import com.laotang.quickdev.hermes.wrapper.ParameterWrapper;

/**
 * Created by Xiaofei on 16/4/8.
 */
public class UtilityGettingReceiver extends Receiver {

    public UtilityGettingReceiver(ObjectWrapper objectWrapper) throws HermesException {
        super(objectWrapper);
        Class<?> clazz = TYPE_CENTER.getClassType(objectWrapper);
        TypeUtils.validateAccessible(clazz);
    }

    @Override
    protected void setMethod(MethodWrapper methodWrapper, ParameterWrapper[] parameterWrappers) {

    }

    @Override
    protected Object invokeMethod() {
        return null;
    }

}
