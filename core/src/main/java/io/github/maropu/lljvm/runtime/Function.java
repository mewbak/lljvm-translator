/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.maropu.lljvm.runtime;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.maropu.lljvm.LLJVMRuntimeException;
import io.github.maropu.lljvm.util.Pair;
import io.github.maropu.lljvm.util.ReflectionUtils;

public class Function {

  private static final Logger logger = LoggerFactory.getLogger(FieldValue.class);

  private static Map<String, Method> externalFuncPointers = new ConcurrentHashMap<>();
  private static LoadingCache<Pair<String, String>, Method> methodCache =
      CacheBuilder.newBuilder()
        .maximumSize(100)
        .build(new CacheLoader<Pair<String, String>, Method>() {

    @Override
    public Method load(Pair<String, String> methodSignature) throws LLJVMRuntimeException {
      final String targetSignature = methodSignature.getKey() + "/" + methodSignature.getValue();
      try {
        Class<?> clazz = ReflectionUtils.getClass(methodSignature.getKey());
        for (Method method : ReflectionUtils.getStaticMethods(clazz)) {
          String candidate = ReflectionUtils.getQualifiedSignature(method);
          if (targetSignature.equals(candidate)) {
            return method;
          }
        }
      } catch (Exception e) {
        // Throws an exception in the end
      }
      throw new LLJVMRuntimeException("Method not found: " + targetSignature);
    }
  });

  private Function() {}

  public static void put(String methodSignature, Method m) {
    externalFuncPointers.put(methodSignature, m);
  }

  public static void remove(String methodSignature) {
    externalFuncPointers.remove(methodSignature);
  }

  public static void clear() {
    externalFuncPointers.clear();
  }

  // TODO: Needs to verify a return type of the given `method`
  private static Object _invoke(Method method, long args) {
    Class<?>[] paramTypes = method.getParameterTypes();
    try {
      if (args != 0) {
        return method.invoke(null, VMemory.unpack(args, paramTypes));
      } else {
        return method.invoke(null, null);
      }
    } catch (IllegalAccessException e) {
      throw new LLJVMRuntimeException("Cannot invoke a method via reflection");
    } catch (InvocationTargetException e) {
      throw new LLJVMRuntimeException(e.getCause().getMessage());
    }
  }

  private static Object _invoke(String className, String methodSignature, long args) {
    if (!className.isEmpty()) {
      synchronized (methodCache) {
        try {
          Method method = methodCache.get(new Pair<>(className, methodSignature));
          logger.debug("Function invoked: signature=" + methodSignature +
            " returnType=" + method.getReturnType().getSimpleName());
          return _invoke(method, args);
        } catch (ExecutionException e) {
          throw new LLJVMRuntimeException("Cannot load a method from the cache");
        }
      }
    } else {
      // Invokes an external function
      if (externalFuncPointers.containsKey(methodSignature)) {
        Method method = externalFuncPointers.get(methodSignature);
        return _invoke(method, args);
      } else {
        throw new LLJVMRuntimeException(
          "Cannot resolve an external function for `" + methodSignature + "`");
      }
    }
  }

  public static void invoke_void(String className, String methodSignature, long args) {
    _invoke(className, methodSignature, args);
  }

  public static void invoke_void(String className, String methodSignature) {
    _invoke(className, methodSignature, 0);
  }

  public static boolean invoke_i1(String className, String methodSignature, long args) {
    return (Boolean) _invoke(className, methodSignature, args);
  }

  public static boolean invoke_i1(String className, String methodSignature) {
    return (Boolean) _invoke(className, methodSignature, 0);
  }

  public static byte invoke_i8(String className, String methodSignature, long args) {
    return (Byte) _invoke(className, methodSignature, args);
  }

  public static byte invoke_i8(String className, String methodSignature) {
    return (Byte) _invoke(className, methodSignature, 0);
  }

  public static short invoke_i16(String className, String methodSignature, long args) {
    return (Short) _invoke(className, methodSignature, args);
  }

  public static short invoke_i16(String className, String methodSignature) {
    return (Short) _invoke(className, methodSignature, 0);
  }

  public static int invoke_i32(String className, String methodSignature, long args) {
    return (Integer) _invoke(className, methodSignature, args);
  }

  public static int invoke_i32(String className, String methodSignature) {
    return (Integer) _invoke(className, methodSignature, 0);
  }

  public static long invoke_i64(String className, String methodSignature, long args) {
    return (Long) _invoke(className, methodSignature, args);
  }

  public static long invoke_i64(String className, String methodSignature) {
    return (Long) _invoke(className, methodSignature, 0);
  }

  public static float invoke_f32(String className, String methodSignature, long args) {
    return (Float) _invoke(className, methodSignature, args);
  }

  public static float invoke_f32(String className, String methodSignature) {
    return (Float) _invoke(className, methodSignature, 0);
  }

  public static double invoke_f64(String className, String methodSignature, long args) {
    return (Double) _invoke(className, methodSignature, args);
  }

  public static double invoke_f64(String className, String methodSignature) {
    return (Double) _invoke(className, methodSignature, 0);
  }
}
